package com.edu.StudyFlow.security;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.TokenInvalidado;
import com.edu.StudyFlow.repository.TokenInvalidadoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Filtro que intercepta toda requisicao antes dela chegar no
 * Controller, verificando se existe um token JWT valido no
 * cabecalho.
 *
 * OncePerRequestFilter garante que este filtro roda apenas
 * uma vez por requisicao.
 *
 * @Component registra esta classe como um bean gerenciado
 * pelo Spring, permitindo que ela seja injetada em outras
 * classes (como o SecurityConfig).
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenInvalidadoRepository tokenInvalidadoRepository;

    // Injecao das dependencias via construtor
    public JwtAuthFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenInvalidadoRepository tokenInvalidadoRepository) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenInvalidadoRepository = tokenInvalidadoRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain)throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // nao possui JWT segue , o spring decide se a rota exige login
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // remove o prefixo "Bearer" para obter o token puro
        String token = authHeader.substring(7);

        // Verifica se o token esta na esta no banco.
        var tokenInvalidado = tokenInvalidadoRepository.findByToken(token);
        if (tokenInvalidado.isPresent()) {
            // Se ja passou da data de expiracao, remove da tabela agora mesmo.
            if (LocalDateTime.now().isAfter(tokenInvalidado.get().getDataExpiracao())) {
                tokenInvalidadoRepository.delete(tokenInvalidado.get());
            }
            // mensagen de erro personalizada
            escreverErro(response, "Token invalido, faça login novamente");
            return;
        }

        // JWT expirado ou inválido
        if (!jwtService.tokenValido(token)) {
            escreverErro(response, "Sessão expirada, faça login novamente");
            return;
        }
        // extrai o email do token e busca os dados do usuario
        String email = jwtService.extrairEmail(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // monta o objeto de autenticacao e registra no contexto de seguranca
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // continua o fluxo normal da requisicao
        filterChain.doFilter(request, response);
    }

    /*
     * Escreve uma resposta de erro em JSON, no mesmo formato usado
     * pelo GlobalExceptionHandler, ja que aqui (dentro do filtro)
     * ele nao consegue interceptar a excecao.
     */
    private void escreverErro(HttpServletResponse response, String mensagem) throws IOException {
        // configuaracao da resposta
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // corpo da mensagem
        String json = String.format("{\"timestamp\":\"%s\",\"status\":401,\"error\":\"token invalido\",\"message\":\"%s\"}",LocalDateTime.now(), mensagem);

        response.getWriter().write(json);
    }
}