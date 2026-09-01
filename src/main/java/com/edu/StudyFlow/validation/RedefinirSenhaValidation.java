package com.edu.StudyFlow.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Validation usado para receber os dados para a validacao de redefinir senha.
 */
public class RedefinirSenhaValidation {
    //@NotBlank garante que o campo nao seja nulo,vazio ou so espacos.

    @NotBlank(message = "O email e obrigatório")
    //@Email valida se o campo esta em um formato de email valido.
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "A senha e obrigatória")
    //@Size serve para definar o tamanho minimo ou maximo do campo.
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "A confirmação de senha e obrigatória")
    private String senhaComparar;

    @NotBlank(message = "O token e obrigatorio")
    private String token;

    // Getters e Setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getSenhaComparar() {
        return senhaComparar;
    }
    public void setSenhaComparar(String senhaComparar) {
        this.senhaComparar = senhaComparar;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

}
