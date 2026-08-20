package com.edu.StudyFlow.service;

import com.edu.StudyFlow.model.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e
 * gerar e validar o codigo de verificacao da autenticacao de dois fatores (2FA).
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class TwoFAService {
    private final EmailService emailService;

    // usar para guardar a quantidade de minutos de duração do codigo.
    private static final int minutosExpirar = 5;

    // Guarda temporariamente o email <chave> e o codigo,data-horario <valor> de 2FA
    private final Map<String, Map> codigosPendentes = new ConcurrentHashMap<>();

    // Injecao do EmailService via construtor
    public  TwoFAService(EmailService emailService) {
        this.emailService = emailService;
    }

    // Gera um codigo de 6 digitos e guarda associado ao email
    public void gerarCodigo (String email){
        // nextInt(900000) gera valores de 0 a 89999, e a soma de 100000 garante que o resultado tenha 6 digitos.
        String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
        Map <String, String> codigoInfo = new ConcurrentHashMap<>();

        // insere o codigo e a data-horario.
        codigoInfo.put("codigo", codigo);
        codigoInfo.put("time", String.valueOf(LocalDateTime.now()));
        codigosPendentes.put(email, codigoInfo);

        // chama o metodo para o envio de email.
        emailService.enviarEmail(email, "Seu código de acesso é: " + codigo
                                            + "\n (Valido por "+minutosExpirar+" minutos)");
    }

    // Verifica se o codigo digitado bate com o que foi gerado
    public boolean validarCodigo (String email, String codigoDigitado) {
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
            codigosPendentes.remove(email);
        }
        return validar;
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
