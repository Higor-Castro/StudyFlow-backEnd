package com.edu.StudyFlow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Intercepta os erros lancados pela aplicacao e transforma
 * em respostas padronizadas.
 *
 * @RestControllerAdvice serve para criar uma classe unica que
 * trata excecoes de forma global para toda a aplicacao,
 * ao inves de tratar erro por erro dentro de cada Controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Erros de validacao do @Valid
     *
     * @ExceptionHandler serve para escolher qual metodo vai tratar
     * um erro especifico que acontecer na aplicacao.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> tratarErroDeValidacao( MethodArgumentNotValidException ex) {

        // Monta o map com o nome do campo -> mensagem de erro
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );
        // Faz o put dos erros
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "erro de validacao");
        body.put("message", erros);
        // envia o erro no formato json
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Erro de regra de negocio lancado manualmente no Service
    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        // Faz o put dos erros
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "requisicao invalida");
        body.put("message", ex.getMessage());
        // envia o erro no formato json
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}