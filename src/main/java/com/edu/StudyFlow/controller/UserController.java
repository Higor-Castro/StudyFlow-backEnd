package com.edu.StudyFlow.controller;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.validation.UserCadastroValidation;
import com.edu.StudyFlow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/*
 * Controller responsavel por receber as requisicoes
 * relacionadas ao usuario.
 *
 * @RestController indica que esta classe responde
 * requisicoes HTTP e que o retorno dos metodos vira direto o
 * corpo da resposta (json).
 *
 * @RequestMapping("/users") define o prefixo de rota:
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    // Injecao do service via construtor
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /*
     * Recebe os dados do usuario, valida e envia para o service salvar
     *
     * @PostMapping: mapeia este metodo para requisicoes POST.
     *
     * @Valid: valida o objeto recebido de acordo com as regras
     * definidas no UserCadastroValidation antes de o metodo ser executado.
     *
     * @RequestBody: converte o JSON recebido no corpo da requisicao
     * automaticamente para um objeto UserCadastroValidation.
     */
    @PostMapping("/cadastro")
    public String criarUsuario(@Valid @RequestBody UserCadastroValidation userValidation){
        userService.salvarUser(userValidation);
        return "Usuario criado com sucesso";
    }
    /*
     * Recebe os dados (email e senha) para validacao do login.
     *
     * @RequestBody Pega o parametro do body do request
     * que vem em forma de json, e converte para um obj Java.
     */
    @PostMapping("/login")
    public String login (@RequestBody UserCadastroValidation userValidation){
        // chama o metodo para validar o usuario.
        boolean validar = userService.validarLogin(userValidation.getEmail(),userValidation.getSenha());
        // verrifica se o usuario e valido.
        if (!validar) {
            throw new RequisicaoInvalidaException("Email ou senha invalida");
        }
        return "Login efetuado com sucesso";

    }

}