package com.edu.StudyFlow.repository;

import com.edu.StudyFlow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Repository serve para fazer a comunicacao entre a
 * aplicacao e a tabela de usuarios no banco de dados.
 *
 * @Repository faz a comunicacao entre a aplicacao e o banco de dados.
 * Obs.: O repositorio deve ser uma interface que estende JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
