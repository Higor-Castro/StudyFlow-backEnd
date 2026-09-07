package com.edu.StudyFlow.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
 * Representa a tabela de consentimentos no banco de dados.
 *
 * @Entity informa ao Spring Data JPA que esta classe
 * representa uma entidade do banco de dados.
 * @Table serve para definir configuracoes da tabela no banco.
 */
@Entity
@Table(name = "consentimentos")
public class Consentimento {
    // @Id Chave primária da tabela
    @Id
    // @GeneratedValue Gera o ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@Column serve para definir configuracoes da coluna
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String versao;
    @Column(nullable = false)
    private String finalidade;
    @Column(nullable = false)
    private boolean revogado;
    @Column(nullable = false)
    private LocalDateTime dataAceite;
    private LocalDateTime dataRevogacao;

    // Construtor vazio
    public Consentimento() {
    }

    // Construtor com parametros
    public Consentimento(String email, String versao, String finalidade, LocalDateTime dataAceite) {
        this.email = email;
        this.versao = versao;
        this.finalidade = finalidade;
        this.dataAceite = dataAceite;
    }
    // Getters e Setters
    public long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersao() {
        return versao;
    }
    public void setVersao(String versao) {
        this.versao = versao;
    }
    public String getFinalidade() {
        return finalidade;
    }
    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }
    public boolean getRevogado() {
        return revogado;
    }
    public void setRevogado(boolean revogado) {
        this.revogado = revogado;
    }
    public LocalDateTime getDataAceite() {
        return dataAceite;
    }
    public void setDataAceite(LocalDateTime dataAceite) {
        this.dataAceite = dataAceite;
    }
    public LocalDateTime getDataRevogacao() {
        return dataRevogacao;
    }
    public void setDataRevogacao(LocalDateTime dataRevogacao) {
        this.dataRevogacao = dataRevogacao;
    }
}
