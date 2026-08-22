package com.edu.StudyFlow.controller;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.service.LogService;
import com.edu.StudyFlow.service.LoginTimeService;
import com.edu.StudyFlow.service.TwoFAService;
import com.edu.StudyFlow.validation.TwoFAValidation;
import com.edu.StudyFlow.validation.UserCadastroValidation;
import com.edu.StudyFlow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    private TwoFAService twoFAService;
    private LogService logService;
    private LoginTimeService loginTimeService;

    // Injecao do service via construtor
    public UserController(UserService userService, TwoFAService twoFAService, LogService logService, LoginTimeService loginTimeService) {
        this.userService = userService;
        this.twoFAService = twoFAService;
        this.logService = logService;
        this.loginTimeService = loginTimeService;
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
        String msg = "";
        try {
            msg = "Usuário criado com sucesso";
            // salva o usuario no banco.
            userService.salvarUser(userValidation);
            // salva os logs na tabela
            Log log = new Log("CADASTRO_SUCESSO", userValidation.getEmail(), msg, LocalDateTime.now());
            logService.salvarLog(log);
            return msg;
        }catch (RequisicaoInvalidaException e) {
            // salva os logs na tabela
            Log log = new Log("CADASTRO_FALHA", userValidation.getEmail(), "CADASTRO FALHA: " + e.getMessage(), LocalDateTime.now());
            logService.salvarLog(log);
            // Devolve a excecao para o ExceptionHandler.
            throw e;
        }
    }
    /*
     * Recebe os dados (email e senha) para validacao do login e geracao do 2FA.
     *
     * @RequestBody Pega o parametro do body do request
     * que vem em forma de json, e converte para um obj Java.
     */
    @PostMapping("/login")
    public String login (@RequestBody UserCadastroValidation userValidation){
        // chama o metodo para validar se o email esta bloqueado.
        boolean validarBloqueio = loginTimeService.estaBloqueado(userValidation.getEmail());
        if(validarBloqueio) {
            throw new RequisicaoInvalidaException("Login esta bloqueado, aguarde o tempo de expiração");
        }
        // chama o metodo para validar o usuario.
        boolean validarUsuario = userService.validarLogin(userValidation.getEmail(),userValidation.getSenha());
        // verrifica se o usuario e valido.
        if (!validarUsuario) {
            // registra tentativa errada para o calculo do bloqueio
            loginTimeService.registrarFalhaLogin(userValidation.getEmail());
            // salva os logs na tabela
            Log log = new Log("LOGIN_FALHA", userValidation.getEmail(), "Email ou senha invalida", LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Email ou senha invalida");
        }
        // Login valido reseta o historico de tentativas erradas
        loginTimeService.retirarBloqueio(userValidation.getEmail());

        // chama o metodo para gerar o codigo autentificacao 2FA.
        twoFAService.gerarCodigo(userValidation.getEmail());
        // salva os logs na tabela
        Log log = new Log("LOGIN_SUCESSO", userValidation.getEmail(), "Email e senha correta 2FA enviado", LocalDateTime.now());
        logService.salvarLog(log);
        return "Email e senha correta. Verifique o código de 2 Fatores";
    }

    // Segunda etapa do login, valida o codigo de 2FA
    @PostMapping("/login/2fa")
    public String loginTwoFA(@Valid @RequestBody TwoFAValidation twoFAValidation){
        // chama o metodo para validar o codigo informado
        boolean validar = twoFAService.validarCodigo(twoFAValidation.getEmail(),twoFAValidation.getCodigo());
        // varrifica se o codigo e valido.
        if (!validar) {
            Log log = new Log("LOGIN_2FA_FALHA", twoFAValidation.getEmail(), "Codigo invalido ou expirado", LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Codigo invalido ou expirado");
        }
        Log log = new Log("LOGIN_2FA_SUCESSO", twoFAValidation.getEmail(), "2FA Correto", LocalDateTime.now());
        logService.salvarLog(log);
        return "login confirmado com sucesso";
    }

}