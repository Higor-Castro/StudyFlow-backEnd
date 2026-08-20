package com.edu.StudyFlow.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e o envio de email.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    // Injecao do mailSender via construtor
    public  EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    // Envia o email com base no email e o texto fornecido via parametro
    public void enviarEmail (String destinatario, String corpo){
        // Cria o objeto que representa o e-mail.
        SimpleMailMessage message = new SimpleMailMessage();

        // Define o destinatario.
        message.setTo(destinatario);

        // Define o assunto.
        message.setSubject("StudyFlow");

        // Define o conteudo.
        message.setText(corpo);

        // Envia o e-mail.
        mailSender.send(message);


    }}
