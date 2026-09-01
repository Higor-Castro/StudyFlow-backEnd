package com.edu.StudyFlow.service;

import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e a
 * logica voltada para a redefinicao de senha.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class RedefinirSenhaService {
    private LogService logService;
    private UserService userService;
    // Injecao do Service via construtor.
    public RedefinirSenhaService(LogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    // valida o token e troca a senha
    public void validarTokenSalvarSenha(String token,String email, String senha, String confirmaSenha){
        userService.redefinirSenha(email,senha,confirmaSenha);
        // salva os logs na tabela
        Log log = new Log("RECUPERACAO_SENHA_SUCESSO", email, "Senha redefinida com sucesso", LocalDateTime.now());
        logService.salvarLog(log);
    }
}
