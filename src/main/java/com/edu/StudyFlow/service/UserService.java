package com.edu.StudyFlow.service;

import com.edu.StudyFlow.validation.UserCadastroValidation;
import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.User;
import com.edu.StudyFlow.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e a
 * logica voltada ao usuario.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    // Injecao do repository e do encoder via construtor.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Valida a confirmacao de senha, gera o hash e salva o usuario.
    public void salvarUser(UserCadastroValidation userValidation) {

        // Verifica se as duas senhas sao iguais.
        if (!userValidation.getSenha().equals(userValidation.getSenhaComparar())) {
            throw new RequisicaoInvalidaException("As senhas não coincidem");
        }

        // Gera o hash da senha com salt unico embutido (BCrypt).
        String senhaHash = passwordEncoder.encode(userValidation.getSenha());

        // Salva os dados do usuario validado com o hash e o salt implementado.
        User user = new User(userValidation.getUsername(), senhaHash, userValidation.getEmail());
        // valida se o email ja esta cadastrado.
        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException e) {
            throw new RequisicaoInvalidaException("Email já cadastrado");
        }

    }

    // valida se o usuario realmente existe.
    public boolean validarLogin (String email, String senha) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RequisicaoInvalidaException("Email ou senha Invalida"));
        return passwordEncoder.matches(senha, user.getPassword());
    }
    // valida as senhas e atualiza ela se o email existir
    public void redefinirSenha (String email, String senha, String confirmaSenha) {
        // validade se as senhas batem
        if (!senha.equals(confirmaSenha)) {
            throw new RequisicaoInvalidaException("As senhas não coincidem");
        }
        // procura o email no banco
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RequisicaoInvalidaException("Usuário não encontrado"));

        // salva a nova senha
        user.setPassword(passwordEncoder.encode(senha));
        userRepository.save(user);
    }
}