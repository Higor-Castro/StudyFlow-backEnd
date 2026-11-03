package com.edu.StudyFlow.service;

import com.edu.StudyFlow.model.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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
    private PasswordEncoder passwordEncoder;

    // usar para guardar a quantidade de minutos de duração do codigo.
    private static final int minutosExpirar = 5;

    // numero maximo de tentativas erradas de validacao do codigo antes de invalida-lo
    // evita forca bruta.
    private static final int tentativasMaxCodigo = 5;

    // Guarda temporariamente o email <chave> e o codigo,data-horario <valor> de 2FA
    private final Map<String, Map> codigosPendentes = new ConcurrentHashMap<>();

    // Injecao do encoder e o service via construtor.
    public  TwoFAService(EmailService emailService, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Gera um codigo de 6 digitos e guarda associado ao email
    public void gerarCodigo (String email){
        // gera um token de 6 digitos usando SecureRandom, um gerador criptograficamente seguro
        String codigo = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        Map<String, Object> codigoInfo = new ConcurrentHashMap<>();

        // guarda o HASH do codigo, data-horario e a quantidade de tentativas.
        codigoInfo.put("codigoHash", passwordEncoder.encode(codigo));
        codigoInfo.put("time", String.valueOf(LocalDateTime.now()));
        codigoInfo.put("tentativas", 0);
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

        LocalDateTime timeCodigo =  LocalDateTime.parse(hashCodigo.get("time").toString());

        //Valida se ja passsou do tempo.
        if (LocalDateTime.now().isAfter(timeCodigo.plusMinutes(minutosExpirar))) {
            codigosPendentes.remove(email);
            return false;
        }

        // Valida se o limite de tentativas erradas ja foi atingido.
        int tentativas = (int) hashCodigo.get("tentativas");
        if (tentativas >= tentativasMaxCodigo) {
            codigosPendentes.remove(email);
            return false;
        }

        // valida se o codigo digitado bate com o HASH armazenado.
        String codigoHash = hashCodigo.get("codigoHash").toString();
        boolean validar = passwordEncoder.matches(codigoDigitado, codigoHash);
        if (validar) {
            codigosPendentes.remove(email);
        }else {
            // soma mais uma tentativa errada antes de permitir a proxima chamada.
            hashCodigo.put("tentativas", tentativas + 1);
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
