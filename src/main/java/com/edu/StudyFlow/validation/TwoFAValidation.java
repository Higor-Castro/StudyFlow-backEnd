package com.edu.StudyFlow.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Validation usado para receber os dados para a validacao 2FA.
 */
public class TwoFAValidation {
    //@NotBlank garante que o campo nao seja nulo,vazio ou so espacos.

    @NotBlank(message = "O email e obrigatório")
    //@Email valida se o campo esta em um formato de email valido.
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "O código é obrigatória")
    //@Size serve para definar o tamanho minimo ou maximo do campo.
    @Size(min = 6, max = 6, message = "O código precisa ter exatamente 6 dígitos")
    private String codigo;

    // Getters e Setters
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getCodigo()
    {
        return codigo;
    }
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

}
