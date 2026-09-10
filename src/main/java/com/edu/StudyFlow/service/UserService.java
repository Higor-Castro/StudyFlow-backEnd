package com.edu.StudyFlow.service;

import com.edu.StudyFlow.model.Consentimento;
import com.edu.StudyFlow.validation.UserCadastroValidation;
import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.User;
import com.edu.StudyFlow.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
    private ConsentimentoService consentimentoService;

    // Injecao do repository,encoder e o service via construtor.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ConsentimentoService consentimentoService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.consentimentoService = consentimentoService;
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
            // salva o user e o consentimento.
            userRepository.save(user);
            consentimentoService.registrarConsentimento(userValidation.getEmail());
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
    // Informa os dados do usuario
    public Map<String, Object> consultarDados(String email) {
        // busca os dados do usuario e o consentimo
        User user = userRepository.findByEmail(email).orElseThrow(()->new RequisicaoInvalidaException("Usuário não encontrado"));
        Optional<Consentimento> consentimento = consentimentoService.buscarConsentimentoAtivo(email);

        Map<String, Object> dadosUsuario = new ConcurrentHashMap<>();
        dadosUsuario.put("nome", user.getUsername());
        dadosUsuario.put("email", user.getEmail());
        dadosUsuario.put("nivel", user.getNivel());
        // verrifica se tem consentimento para inserir
        if (consentimento.isPresent()) {
            Map<String,Object> dadosConsentimento = new ConcurrentHashMap<>();
            dadosConsentimento.put("versaoTermos", consentimento.get().getVersao());
            dadosConsentimento.put("dataAceite", consentimento.get().getDataAceite());
            dadosConsentimento.put("finalidade", consentimento.get().getFinalidade());
            dadosUsuario.put("consentimento", dadosConsentimento);
        }
        return dadosUsuario;
    }
}