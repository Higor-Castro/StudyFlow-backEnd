package com.edu.StudyFlow.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

/*
 * Security tem a responsabilidade pela seguranca da aplicacao
 *
 * @Configuration indica que esta classe
 * contem configuracoes que o Spring deve carregar
 * ao iniciar a aplicacao.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Injecao do filtro JWT via construtor
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
        http
                // desativa a protecao CSRF, ja que a API e stateless (sem sessao de formulario)
                .csrf(csrf -> csrf.disable())
                // aplica a configuracao de CORS definida em corsConfigurationSource()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // nao guarda sessao no servidor: cada requisicao se autentica pelo token JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rotas publicas: cadastro e as duas etapas do login
                        .requestMatchers("/users/cadastro", "/users/login", "/users/login/2fa", "/users/senha/redefinir").permitAll()
                        // Rotas de admin: exigem usuario com nivel ADMIN, tambem precisa do jwt
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // todas as demais rotas exigem apenas estar autenticado
                        .anyRequest().authenticated()
                )
                // Insere o filtro JWT antes do filtro padrao de autenticacao do Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // Libera o navegador a acessar apenas as rotas /users
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/users/**", config);
        return source;
    }
    /*
     * Gera o hash das senhas com BCrypt.
     * O parametro (12) e o custo: quanto maior, mais lento fica.
     * O calculo do hash e mais seguro contra ataques de forca bruta.
     * O valor 12 e um equilibrio comum entre seguranca e tempo de resposta aceitavel.
     *
     * @Bean indica que o retorno deste metodo sera gerenciado
     * pelo Spring, podendo ser injetado em outras classes.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}