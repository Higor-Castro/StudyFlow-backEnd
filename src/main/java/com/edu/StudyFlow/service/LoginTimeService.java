package com.edu.StudyFlow.service;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Log;
import com.edu.StudyFlow.model.LoginTentativa;
import com.edu.StudyFlow.repository.LoginTentativaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * Service tem o papel de concentrar a logica da aplicacao,
 * onde o foco dessa classe e controlar a tentativas de login
 * bloqueando o login temporariamente apos varias tentativas erradas.
 *
 * @Service indica que esta classe contem
 * a logica de negocio da aplicacao.
 */
@Service
public class LoginTimeService {
    // duracao de bloqueio e quantidade de tentativas
    private static final int tentativasMax = 5;
    private static final int bloqueioMinutos = 10;

    private final LogService logService;
    private final LoginTentativaRepository loginTentativaRepository;

    // Injecao do LogService via construtor
    public LoginTimeService(LogService logService, LoginTentativaRepository loginTentativaRepository) {
        this.logService = logService;
        this.loginTentativaRepository = loginTentativaRepository;

    }

    // verrifica se o email esta bloqueado
    public boolean estaBloqueado(String email, String ip) {

        Optional<LoginTentativa> bloqueado = loginTentativaRepository.findByEmailAndIp(email,ip);
        // se nao estiver na lista e pq nao esta bloqueado
        if (bloqueado.isEmpty()) return false;

        LoginTentativa tentativa = bloqueado.get();
        LocalDateTime bloqueadoAte = tentativa.getBloqueadoAte();

        // se não tiver valor e pq nao esta bloqueado
        if (bloqueadoAte == null) return false;

        // valida se passou o tempo de bloqueio
        if(!LocalDateTime.now().isBefore(bloqueadoAte)) {
            retirarBloqueio(email,ip);
            // salva os logs na tabela
            Log log = new Log("LOGIN_DESBLOQUEADO_SUCESSO", email, "Bloqueio expirado, usuario liberado para novas tentativas",LocalDateTime.now());
            logService.salvarLog(log);
            return false;
        }
        // retorna true se estiver bloqueado
        return true;
    }

    // Registrar a quantidades de tentativas erradas
    public void registrarFalhaLogin (String email, String ip) {
        LoginTentativa tentativa = loginTentativaRepository.findByEmailAndIp(email,ip).orElseGet(() -> new LoginTentativa(email, 0, ip));

        // pega a quantidade de tentativas e acresenta uma nova tentativa
        int total = tentativa.getTentativas() + 1;
        tentativa.setTentativas(total);

        // verrifica se ultrapasou a quantidade de tentativas
        if(total >= tentativasMax){

            LocalDateTime agora = LocalDateTime.now();

            // Define o período de bloqueio e Salva
            tentativa.setBloqueadoAte(agora.plusMinutes(bloqueioMinutos));
            loginTentativaRepository.save(tentativa);

            // salva os logs na tabela
            Log log = new Log("LOGIN_BLOQUEADO_FALHA", email,
                    "Bloqueado por " + bloqueioMinutos + " minutos apos " + total + " tentativas erradas",
                              agora);
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Muitas tentativas erradas. Login bloqueado por " + bloqueioMinutos + " minutos");
        }

        // Salva o bloqueio no banco de dados
        loginTentativaRepository.save(tentativa);
    }

    // Remove o contador e o bloqueio apos login valido
    public void retirarBloqueio (String email, String ip) {
        loginTentativaRepository.deleteByEmailAndIp(email,ip);
    }

    /*
     * Remove os email que ja expiraram, mesmo que
     * o usuario nunca tenha tendado validado, roda a cada 5 minutos
     *
     * @Scheduled faz com que o Spring chama esse metodo automaticamente
     * precisa passar fixedRate para falar de quanto em quanto tempo vai ser executado.
     */
    @Scheduled(fixedRate = 300000 )
    public void limparBloqueiosExpirados () {
        LocalDateTime agora = LocalDateTime.now();
        var expirados = loginTentativaRepository.findAllByBloqueadoAteBefore(agora);
        for (LoginTentativa tentativa : expirados) {
            Log log = new Log("LOGIN_DESBLOQUEADO_SUCESSO", tentativa.getEmail(), "Bloqueio expirado, removido automaticamente", agora);
            logService.salvarLog(log);
        }
        loginTentativaRepository.deleteAll(expirados);
    }

}
