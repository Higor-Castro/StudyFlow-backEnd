package com.edu.StudyFlow.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/*
 * Representa a tabela de LoginTentativa no banco de dados.
 *
 * @Entity informa ao Spring Data JPA que esta classe
 * representa uma entidade do banco de dados.
 * @Table serve para definir configuracoes da tabela no banco.
 */

@Entity
@Table(
        name = "login_tentativa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "ip"})
)
public class LoginTentativa {
    // @Id Chave primária da tabela
    @Id
    // @GeneratedValue Gera o ID automaticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column serve para definir configuracoes da coluna
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer tentativas = 0;

    @Column(nullable = false)
    private String ip;

    private LocalDateTime bloqueadoAte;


    // Construtor vazio
    public LoginTentativa() {

    }

    // Construtor com parametros
    public LoginTentativa(String email, Integer tentativas, String ip) {
        this.email = email;
        this.tentativas = tentativas;
        this.ip = ip;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getTentativas() {
        return tentativas;
    }
    public void setTentativas(Integer tentativas){
        this.tentativas = tentativas;
    }
    public LocalDateTime getBloqueadoAte() {
        return bloqueadoAte;
    }
    public void setBloqueadoAte(LocalDateTime bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
}
