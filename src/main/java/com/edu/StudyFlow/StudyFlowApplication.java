package com.edu.StudyFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * @SpringBootApplication inicia e configura
 * automaticamente a aplicacao Spring Boot.
 */

@SpringBootApplication
public class StudyFlowApplication {
	// Metodo principal responsavel por iniciar a aplicacao.
	public static void main(String[] args) {
		SpringApplication.run(StudyFlowApplication.class, args);
	}

}

