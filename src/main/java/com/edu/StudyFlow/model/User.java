package com.edu.StudyFlow.model;

import jakarta.persistence.*;

/*
 * Representa a tabela de users no banco de dados.
 *
 * @Entity informa ao Spring Data JPA que esta classe
 * representa uma entidade do banco de dados.
 * @Table serve para definir configuracoes da tabela no banco.
 */
@Entity
@Table(name = "users")
public class User {

    // @Id Chave primária da tabela
    @Id
    // @GeneratedValue Gera o ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@Column serve para definir configuracoes da coluna
    @Column (name = "nome")
    private String username;
    @Column (name = "senha")
    private String password;

    // o nome email ja e igual o da (unique = true), so para documentar.
    // já esta registrado no banco
    @Column (unique = true)
    private String email;

    // Construtor vazio
    public User() {
    }
    // Construtor com parametros
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}