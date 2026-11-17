package com.edu.StudyFlow.repository;

import com.edu.StudyFlow.model.LoginTentativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Repository serve para fazer a comunicacao entre a
 * aplicacao e a tabela de tokensInvalidados no banco de dados.
 *
 * @Repository faz a comunicacao entre a aplicacao e o banco de dados.
 * Obs.: O repositorio deve ser uma interface que estende JpaRepository.
 */
@Repository
public interface LoginTentativaRepository extends JpaRepository<LoginTentativa, Long> {
    /*
     * Busca uma tentativa pela combinação de email e IP.
     *
     * Optional serve para representar um valor que pode ou nao existir
     * ajutando a evitar retornar null quando nao encontrar o valor.
     */
    Optional<LoginTentativa> findByEmailAndIp(String email, String ip);

    // Busca tentativas com bloqueio expirado.
    List<LoginTentativa> findAllByBloqueadoAteBefore(LocalDateTime data);

    // Exclui o registro pela combinação de email e IP.
    void deleteByEmailAndIp(String email, String ip);
}
