package com.edu.StudyFlow.service;

import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.repository.LogRepository;
import org.springframework.stereotype.Service;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e a
 * logica voltada para utilizacao dos logs da aplicacao.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class LogService {
    private LogRepository logRepository;

    // Injecao do repository via construtor.
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // salva os logs da aplicacao
    public void salvarLog (Log log){
        logRepository.save(log);
    }
}
