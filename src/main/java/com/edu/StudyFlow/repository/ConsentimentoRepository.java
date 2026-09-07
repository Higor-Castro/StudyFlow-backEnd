package com.edu.StudyFlow.repository;

import com.edu.StudyFlow.model.Consentimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Repository serve para fazer a comunicacao entre a
 * aplicacao e a tabela de usuarios no banco de dados.
 *
 * @Repository faz a comunicacao entre a aplicacao e o banco de dados.
 * Obs.: O repositorio deve ser uma interface que estende JpaRepository.
 */
@Repository
public interface ConsentimentoRepository extends JpaRepository<Consentimento,Long> {
    /*
     * Busca um usuario pelo email.
     *
     * List, pois pode existir versões futuras dos termos, ou novos termos.
     */
    List<Consentimento> findByEmail(String email);
    /*
     * Busca o consentimento mais recente e ativo.
     *
     * Optional serve para representar um valor que pode ou nao existir
     * ajutando a evitar retornar null quando nao encontrar o valor.
     */
    @Query("SELECT c FROM Consentimento c WHERE c.email = :email AND c.revogado = false ORDER BY c.dataAceite DESC")
    Optional<Consentimento> buscarAtivo(@Param("email") String email);
}
