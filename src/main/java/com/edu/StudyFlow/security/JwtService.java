package com.edu.StudyFlow.security;

import com.edu.StudyFlow.model.TokenInvalidado;
import com.edu.StudyFlow.repository.TokenInvalidadoRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
/*
 * Service responsavel por gerar, validar e invalidar
 * os tokens JWT usados como sessao apos o login.
 *
 *  @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class JwtService {

    private final SecretKey chave;
    private final TokenInvalidadoRepository tokenInvalidadoRepository;

    private static final long minutosExpirar = 30;

    // Gera a chave de assinatura a partir do application.properties
    public JwtService(@Value("${jwt.secret}") String secret, TokenInvalidadoRepository tokenInvalidadoRepository) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenInvalidadoRepository = tokenInvalidadoRepository;
    }
    // gera um token JWT valido com tempo de expiracao estimado a cima, com o email como subject
    public String gerarToken(String email) {

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + minutosExpirar * 60 * 1000);
        // retorna o token valido e o email como chave para saber de quem e o token
        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    // extrai o email de dentro do token
    public String extrairEmail(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    // verifica se o token tem assinatura valida e ainda nao expirou
    public boolean tokenValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    // marca o token como invalidado, guardando quando ele expiraria naturalmente
    public void salvarLogout (String token) {
        Date expiracao = Jwts.parser()
                        .verifyWith(chave)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();
        // paga o tempo de expiracao do token
        LocalDateTime dataExpiracao = expiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        tokenInvalidadoRepository.save(new TokenInvalidado(token, dataExpiracao));
    }
    /*
     * Remove da tabela os tokens invalidados expirados,
     * para não ter um acumulo desnecessario de tokens. Roda a cada 3 horas
     *
     * @Scheduled faz com que o Spring chama esse metodo automaticamente
     * precisa passar fixedRate para falar de quanto em quanto tempo vai ser executado.
     */
    @Scheduled(fixedRate = 10800000)
    public void limparTokensExpirados() {
        tokenInvalidadoRepository.removerExpirados(LocalDateTime.now());
    }
}