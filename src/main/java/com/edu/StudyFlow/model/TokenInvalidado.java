package com.edu.StudyFlow.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
 * Representa a tabela de tokensInvalidados no banco de dados.
 *
 * @Entity informa ao Spring Data JPA que esta classe
 * representa uma entidade do banco de dados.
 * @Table serve para definir configuracoes da tabela no banco.
 */

@Entity
@Table(name = "tokensInvalidados")
public class TokenInvalidado {
    // @Id Chave primária da tabela
    @Id
    // @GeneratedValue Gera o ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column serve para definir configuracoes da coluna
    @Column(length = 512, nullable = false, unique = true)
    private String token;

    // Data em que o token expiraria naturalmente, usada para limpar a tabela depois
    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    // Construtor vazio
    public TokenInvalidado() {
    }
    // Construtor com parametros
    public TokenInvalidado(String token, LocalDateTime dataExpiracao) {
        this.token = token;
        this.dataExpiracao = dataExpiracao;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }
    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}