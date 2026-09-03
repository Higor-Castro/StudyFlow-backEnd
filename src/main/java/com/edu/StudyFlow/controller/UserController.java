package com.edu.StudyFlow.controller;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.security.JwtService;
import com.edu.StudyFlow.service.*;
import com.edu.StudyFlow.validation.RedefinirSenhaValidation;
import com.edu.StudyFlow.validation.TwoFAValidation;
import com.edu.StudyFlow.validation.UserCadastroValidation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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
    private JwtService jwtService;
    private RedefinirSenhaService redefinirSenhaService;

    // Injecao do service via construtor
    public UserController(UserService userService, TwoFAService twoFAService, LogService logService,
                          LoginTimeService loginTimeService, JwtService jwtService, RedefinirSenhaService redefinirSenhaService) {
        this.userService = userService;
        this.twoFAService = twoFAService;
        this.logService = logService;
        this.loginTimeService = loginTimeService;
        this.jwtService = jwtService;
        this.redefinirSenhaService = redefinirSenhaService;
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

    //Recebe os dados (email e senha) para validacao do login e geracao do 2FA..
    @PostMapping("/login")
    public String login (@Valid @RequestBody UserCadastroValidation userValidation){
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
    public Map <String,String> loginTwoFA(@Valid @RequestBody TwoFAValidation twoFAValidation){
        // chama o metodo para validar o codigo informado
        boolean validar = twoFAService.validarCodigo(twoFAValidation.getEmail(),twoFAValidation.getCodigo());
        // varrifica se o codigo e valido.
        if (!validar) {
            // salva os logs na tabela
            Log log = new Log("LOGIN_2FA_FALHA", twoFAValidation.getEmail(), "Codigo invalido ou expirado", LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Codigo invalido ou expirado");
        }
        // salva os logs na tabela
        Log log = new Log("LOGIN_2FA_SUCESSO", twoFAValidation.getEmail(), "2FA Correto", LocalDateTime.now());
        logService.salvarLog(log);
        // Gera o JWT após o 2FA ser validado
        String token = jwtService.gerarToken( twoFAValidation.getEmail());
        return Map.of("mensagem", "Login realizado com sucesso",
                      "token", token);
    }
     // Encerra a sessao do usuario (logout), invalidando o token JWT atual para que ele nao possa mais ser usado.
    @PostMapping("/logout")
    public String logout( @RequestHeader("Authorization") String authHeader) {
        // Valida se o cabecalho veio no formato esperado
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RequisicaoInvalidaException("Token não informado");
        }
        // Remove o prefixo "Bearer " para obter o token puro
        String token = authHeader.substring(7);
        String email = jwtService.extrairEmail(token);

        // Marca o token como invalidado, impedindo seu uso futuro
        jwtService.salvarLogout(token);

        // salva os logs na tabela
        Log log = new Log("LOGOUT_SUCESSO", email, "Sessao invalidada via logout", LocalDateTime.now());
        logService.salvarLog(log);

        return "Logout realizado com sucesso";
    }
    // envia o token para email para recuperacao de senha
    @PostMapping("/senha/recuperar")
    public String solicitarRecuperacao(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        // envia o email para enviar o token.
        redefinirSenhaService.enviarToken(email);
        return "Token foi enviado para o email informado.";
    }
    // valida o token antes de redefinir a senha
    @PostMapping("/senha/validar")
    public String validarToken(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String token = body.get("token");
        // chama o metodo para validar o codigo informado
        boolean validar = redefinirSenhaService.validarToken(email, token);
        // varrifica se o codigo e valido.
        if (!validar) {
            // salva os logs na tabela
            Log log = new Log("RECUPERACAO_CODIGO_FALHA", email, "Token invalido ou expirado", LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Token invalido ou expirado");
        }
        return "Token valido";
    }

    // Segunda etapa da recuperacao valida o token e redefine a senha
    @PostMapping("/senha/redefinir")
    public String recuperarSenha(@Valid @RequestBody RedefinirSenhaValidation redefinirSenhaValidation) {
        // valida e recupera a senha
        redefinirSenhaService.validarTokenSalvarSenha(redefinirSenhaValidation.getToken(),redefinirSenhaValidation.getEmail(),
                                                      redefinirSenhaValidation.getSenha(),redefinirSenhaValidation.getSenhaComparar());
        return "Senha redefinida com sucesso";
    }

    // validar a secao do user
    @GetMapping("/validar")
    public String validar() {
        return "Token válido";
    }
}