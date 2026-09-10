package com.edu.StudyFlow.repository;

import com.edu.StudyFlow.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/*
 * Repository serve para fazer a comunicacao entre a
 * aplicacao e a tabela de logs no banco de dados.
 *
 * @Repository faz a comunicacao entre a aplicacao e o banco de dados.
 * Obs.: O repositorio deve ser uma interface que estende JpaRepository.
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    /*
     * Anonimiza os logs do usuario
     *
     * @Modifying informa que esta @Query nao e um SELECT, e sim uma
     * operacao que altera dados
     *
     * @Transactional e obrigatorio para @Modifying, pois toda alteracao
     * no banco precisa acontecer dentro de uma transacao.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Log l SET l.email = 'usuario-excluido' WHERE l.email = :email")
    void anonimizarPorEmail(@Param("email") String email);
}
