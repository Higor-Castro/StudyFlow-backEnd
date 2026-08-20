package com.edu.StudyFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * @SpringBootApplication inicia e configura
 * automaticamente a aplicacao Spring Boot.
 *
 * @EnableScheduling ativa o suporte a tarefas agendadas
 * (metodos com @Scheduled) na aplicacao.
 */

@SpringBootApplication
@EnableScheduling
public class StudyFlowApplication {
	// Metodo principal responsavel por iniciar a aplicacao.
	public static void main(String[] args) {
		SpringApplication.run(StudyFlowApplication.class, args);
	}

}

