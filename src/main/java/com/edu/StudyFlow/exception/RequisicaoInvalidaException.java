package com.edu.StudyFlow.exception;

/*
 * Excecao lancada quando uma regra e violada,
 * como por exemplo quando a senha e a confirmacao nao coincidem.
 * O GlobalExceptionHandler intercepta esta excecao e devolve
 * uma resposta padronizada ao cliente.
 */

public class RequisicaoInvalidaException extends RuntimeException {
    public RequisicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
