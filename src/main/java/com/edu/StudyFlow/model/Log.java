package com.edu.StudyFlow.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
 * Representa a tabela de logs no banco de dados.
 *
 * @Entity informa ao Spring Data JPA que esta classe
 * representa uma entidade do banco de dados.
 * @Table serve para definir configuracoes da tabela no banco.
 */
@Entity
@Table(name = "logs")
public class Log {

    // @Id Chave primária da tabela
    @Id
    // @GeneratedValue Gera o ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@Column serve para definir configuracoes da coluna
    @Column(name = "tipo")
    private String type;
    @Column(name = "email")
    private String email;
    @Column(name = "mensagem")
    private String mensagem;
    @Column(name = "data_hora")
    private LocalDateTime dateTime;

    // Construtor vazio
    public Log() {}
    // Construtor com parametros
    public Log(String type, String email, String mensagem, LocalDateTime dateTime) {
        this.type = type;
        this.email = email;
        this.mensagem = mensagem;
        this.dateTime = dateTime;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
