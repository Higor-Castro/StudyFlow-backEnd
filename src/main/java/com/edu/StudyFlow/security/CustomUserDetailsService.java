package com.edu.StudyFlow.security;

import com.edu.StudyFlow.model.User;
import com.edu.StudyFlow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/*
 * Conecta o Spring  ao banco de dados,sem esta
 * classe o Spring nao saberia onde buscar os usuarios
 * para autenticar via JWT.
 *
 *  * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // injecao do repository via construtor
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * busca o usuario pelo email e monta o objeto que o Sprin entende
     *
     * @Override indica que este metodo esta substituindo
     * um metodo ja definido na interface ou classe pai.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // busca o usuario pelo email, ou lanca erro se nao existir
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        // monta o objeto UserDetails que o Spring entende
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
