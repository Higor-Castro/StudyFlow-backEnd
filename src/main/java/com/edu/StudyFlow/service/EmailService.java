package com.edu.StudyFlow.service;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/*
 * Service tem o papel de concentrar a logica da aplicacao,
 * onde o papal e  enviar emails atraves da API do Brevo.
 * (plano gratuito do Render bloqueia smtp).
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class EmailService {
    // @Value injeta o valor de uma propriedade

    // Chave da API do Brevo, configurada via application.properties
    @Value("${brevo.api-key}")
    private String apiKey;

    // Email remetente, precisa ser o mesmo email da conta Brevo
    @Value("${brevo.email}")
    private String emailRemetente;

    private final RestTemplate restTemplate = new RestTemplate();

    // Envia um email com base no destinatario e no texto fornecido via parametro
    public void enviarEmail(String destinatario, String corpo) {

        String url = "https://api.brevo.com/v3/smtp/email";

        // Monta os headers exigidos pela API do Brevo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey.trim());

        // Monta o remetente
        Map<String, Object> remetente = new HashMap<>();
        remetente.put("email", emailRemetente);
        remetente.put("name", "StudyFlow");

        // Monta o destinatario
        Map<String, Object> destinatarioMap = new HashMap<>();
        destinatarioMap.put("email", destinatario);

        // Monta o corpo da requisicao no formato esperado pela API
        Map<String, Object> body = new HashMap<>();
        body.put("sender", remetente);
        body.put("to", java.util.List.of(destinatarioMap));
        body.put("subject", "StudyFlow");
        body.put("textContent", corpo);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Envia a requisicao para a API do Brevo
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RequisicaoInvalidaException("Falha ao enviar email: " + e.getMessage());
        }
    }
}