package com.edu.StudyFlow.repository;

import com.edu.StudyFlow.model.TokenInvalidado;
import com.edu.StudyFlow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * Repository serve para fazer a comunicacao entre a
 * aplicacao e a tabela de tokensInvalidados no banco de dados.
 *
 * @Repository faz a comunicacao entre a aplicacao e o banco de dados.
 * Obs.: O repositorio deve ser uma interface que estende JpaRepository.
 */
@Repository
public interface TokenInvalidadoRepository extends JpaRepository<TokenInvalidado, Long> {
    /*
     * busca o registro completo do token.
     *
     * Optional serve para representar um valor que pode ou nao existir
     * ajutando a evitar retornar null quando nao encontrar o valor.
     */
    Optional<TokenInvalidado> findByToken(String token);
    /*
     * Remove os tokens cuja data de expiracao ja passou
     *
     * @Query define manualmente a consulta.
     *
     * @Modifying informa que essa @Query nao e um SELECT, e sim uma
     * operacao que altera dados (DELETE, UPDATE)
     *
     * @Transactional e obrigatorio para @Modifying, pois toda alteracao
     * no banco (DELETE/UPDATE) precisa acontecer dentro de uma transacao.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenInvalidado t WHERE t.dataExpiracao < :agora")
    void removerExpirados(LocalDateTime agora);

}