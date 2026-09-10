package com.edu.StudyFlow.service;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Consentimento;
import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.repository.ConsentimentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * Service tem o papel de concentrar
 * a logica da aplicacao, onde o foco dessa classe e a
 * logica voltada em  registrar e revogar o consentimento
 * do usuario para o tratamento de seus dados pessoais.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class ConsentimentoService {
    // finalidade padrao do sistema e a versao atual
    private static final String versaoAtual = "1.0";
    private static final String finalidadePadrao = "Autenticacao e funcionamento da plataforma StudyFlow (cadastro, login, seguranca da conta)";

    private final ConsentimentoRepository consentimentoRepository;
    private final LogService logService;
    // Injecao do repository e do log via construtor.
    public ConsentimentoService(ConsentimentoRepository consentimentoRepository, LogService logService) {
        this.consentimentoRepository = consentimentoRepository;
        this.logService = logService;
    }
    // Registra o consentimento no momento do cadastro
    public void registrarConsentimento(String email){
        // salva o consentimento na tabela
        Consentimento consentimento = new Consentimento(email,versaoAtual,finalidadePadrao, LocalDateTime.now());
        consentimentoRepository.save(consentimento);
        // salva o log na tabela
        Log log = new Log("CONSENTIMENTO_REGISTRADO_SUCESSO", email,"Consentimento registrado (versao " + versaoAtual + ")", LocalDateTime.now());
        logService.salvarLog(log);
    }
    // Revoga o consentimento do ativo usuario
    public void revogarConsentimento(String email){
        Consentimento consentimento = consentimentoRepository.buscarAtivo(email)
                                      .orElseThrow(()-> new RequisicaoInvalidaException("Nenhum consentimento ativo encontrado"));
        consentimento.setRevogado(true);
        consentimento.setDataRevogacao(LocalDateTime.now());
        consentimentoRepository.save(consentimento);
        Log log = new Log("CONSENTIMENTO_REVOGADO_SUCESSO", email, "Consentimento revogado pelo usuario", LocalDateTime.now());
        logService.salvarLog(log);

    }
    // verrifica se tem consentimento ativo
    public boolean temConsentimentoAtivo(String email) {
        return consentimentoRepository.buscarAtivo(email).isPresent();
    }
    // Retorna o consentimento ativo do usuario
    public Optional<Consentimento> buscarConsentimentoAtivo(String email) {
        return consentimentoRepository.buscarAtivo(email);
    }



}
