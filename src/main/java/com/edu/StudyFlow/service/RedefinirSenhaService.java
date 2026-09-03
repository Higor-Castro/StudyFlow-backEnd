package com.edu.StudyFlow.service;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e a
 * logica voltada para a redefinicao de senha.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class RedefinirSenhaService {
    // usar para guardar a quantidade de minutos de duração do codigo.
    private static final int minutosExpirar = 5;

    // Guarda temporariamente o email <chave> e o codigo,data-horario <valor> de 2FA
    private final Map<String, Map> codigosPendentes = new ConcurrentHashMap<>();

    private LogService logService;
    private UserService userService;
    private UserRepository userRepository;
    private EmailService emailService;


    // Injecao do Service via construtor.
    public RedefinirSenhaService(LogService logService, UserService userService,UserRepository userRepository, EmailService emailService) {
        this.logService = logService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    // gera o token para a recuperacao da senha
    public void enviarToken(String email){
        // verrifica se o email existe no banco.
        boolean existe = userRepository.findByEmail(email).isPresent();
        if (existe) {
            // gera um token de 6 digitos usando SecureRandom, um gerador criptograficamente seguro
            String codigo = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
            // insere o codigo e a data-horario.
            Map<String, String> tokenInfo = new ConcurrentHashMap<>();
            tokenInfo.put("codigo", codigo);
            tokenInfo.put("time", String.valueOf(LocalDateTime.now()));
            tokenInfo.put("validado", "false");
            codigosPendentes.put(email, tokenInfo);
            // chama o metodo para o envio de email
            emailService.enviarEmail(email, "Seu código de recuperação de senha: " + codigo
                    + "\n (Valido por " + minutosExpirar + " minutos)");
        }
        // salva os logs na tabela
        Log log = new Log("RECUPERACAO_CODIGO_SUCESSO", email, "Solicitacao de recuperacao de senha", LocalDateTime.now());
        logService.salvarLog(log);

    }
    // Verifica se o codigo digitado bate com o que foi gerado
    public boolean validarToken(String email,String codigoDigitado) {
        // procura na lista o codigo e a data-horario referente ao email.
        Map hashCodigo = codigosPendentes.get(email);
        // Verrica se tem valor null.
        if(hashCodigo == null) {
            return false;
        }
        String codigoCorreto = hashCodigo.get("codigo").toString();
        LocalDateTime timeCodigo =  LocalDateTime.parse(hashCodigo.get("time").toString());
        //Valida se ja passsou do tempo.
        if (LocalDateTime.now().isAfter(timeCodigo.plusMinutes(minutosExpirar))) {
            codigosPendentes.remove(email);
            return false;
        }
        // valida o se o codigo ditado e igual ao gerado.
        boolean validar = codigoCorreto.equals(codigoDigitado);
        if (validar) {
            // Altera o registro que está dentro do Map principal
            hashCodigo.put("validado", "true");
            // salva os logs na tabela
            Log log = new Log("RECUPERACAO_CODIGO_VALIDADO_SUCESSO", email, "Codigo validado com sucesso", LocalDateTime.now());
            logService.salvarLog(log);
        }
        return validar;
    }

    // valida o token e troca a senha
    public void validarTokenSalvarSenha(String token,String email, String senha, String confirmaSenha){
        // Reaproveita a mesma validacao (codigo correto + nao expirado)
        boolean valido = validarToken(email, token);
        if (!valido) {
            Log log = new Log("RECUPERACAO_SENHA_FALHA", email, "Token invalido ou expirado", LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Token invalido ou expirado");
        }
        // Remove o token após a redefinição da senha
        codigosPendentes.remove(email);
        // Redefine a senha
        userService.redefinirSenha(email, senha, confirmaSenha);
        // salva os logs na tabela
        Log log = new Log("RECUPERACAO_SENHA_SUCESSO", email, "Senha redefinida com sucesso", LocalDateTime.now());
        logService.salvarLog(log);
    }
    /*
     * Remove os codigo que ja expiraram, mesmo que
     * o usuario nunca tenha tendado validado, roda a cada 5 minutos
     *
     * @Scheduled faz com que o Spring chama esse metodo automaticamente
     * precisa passar fixedRate para falar de quanto em quanto tempo vai ser executado.
     */
    @Scheduled(fixedRate = 300000 )
    public void limparCodigosExpirados () {
        // Percorre os codigos pendentes e remove aqueles que ja expiraram.
        codigosPendentes.entrySet().removeIf( valores -> {
            Map info = (Map)  valores.getValue();
            LocalDateTime timeCodigo = LocalDateTime.parse(info.get("time").toString());
            return LocalDateTime.now().isAfter(timeCodigo.plusMinutes(minutosExpirar));
        });
    }
}
