package com.edu.StudyFlow.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller responsavel por receber as requisicoes
 * relacionadas ao usuario do nivel admin.
 *
 * @RestController indica que esta classe responde
 * requisicoes HTTP e que o retorno dos metodos vira direto o
 * corpo da resposta (json).
 *
 * @RequestMapping("/admin") define o prefixo de rota:
 */
@RestController
@RequestMapping("/admin")
public class UserAdminController {
    // teste para admin
    @GetMapping("/teste")
    public String validar() {
        return "User Admin";
    }

}
