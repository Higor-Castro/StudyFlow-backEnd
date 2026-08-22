package com.edu.StudyFlow.service;

import com.edu.StudyFlow.exception.RequisicaoInvalidaException;
import com.edu.StudyFlow.model.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    //Guarda o email <chave> e as tentativas<valor>
    private final Map<String, Integer> tentativas = new ConcurrentHashMap<>();
    //Guarda o email <chave> e a data-horario do bloqueio <valor>
    private final Map<String, LocalDateTime> bloqueios = new ConcurrentHashMap<>();
    // Injecao do LogService via construtor
    public LoginTimeService(LogService logService) {
        this.logService = logService;
    }
    // verrifica se o email esta bloqueado
    public boolean estaBloqueado(String email) {
        LocalDateTime bloqueado = bloqueios.get(email);
        // se nao estiver na lista e pq nao esta bloqueado
        if (bloqueado == null) return false;
        // valida se passou o tempo de bloqueio
        if(LocalDateTime.now().isAfter(bloqueado.plusMinutes(bloqueioMinutos))) {
            retirarBloqueio(email);
            // salva os logs na tabela
            Log log = new Log("LOGIN_DESBLOQUEADO_SUCESSO", email,
                    "Bloqueio expirado, usuario liberado para novas tentativas",
                    LocalDateTime.now());
            logService.salvarLog(log);
            return false;
        }
        // retorna true se estiver bloqueado
        return true;
    }

    // Registrar a quantidades de tentativas erradas
    public void registrarFalhaLogin (String email){
        // pega a quantidade de tentativas e acresenta uma nova tentativa
        int total = tentativas.getOrDefault(email, 0) + 1;
        tentativas.put(email, total);
        // verrifica se ultrapasou a quantidade de tentativas
        if(total >= tentativasMax){
            //Adiciona o bloqueio
            bloqueios.put(email, LocalDateTime.now());
            // salva os logs na tabela
            Log log = new Log("LOGIN_BLOQUEADO_FALHA", email,
                    "Bloqueado por " + bloqueioMinutos + " minutos apos " + total + " tentativas erradas",
                              LocalDateTime.now());
            logService.salvarLog(log);
            throw new RequisicaoInvalidaException("Muitas tentativas erradas. Login bloqueado por " + bloqueioMinutos + " minutos");
        }
    }
    // login feito pode retirar as tentativas
    public void retirarBloqueio (String email){
        bloqueios.remove(email);
        tentativas.remove(email);
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
        // Percorre os email pendentes e remove aqueles que ja expiraram.
        bloqueios.entrySet().removeIf( valores -> {
            boolean expirado = LocalDateTime.now().isAfter(valores.getValue().plusMinutes(bloqueioMinutos));
            if(expirado){
                // remove o contador de tentativas do email
                tentativas.remove(valores.getKey());
                // Registra a liberacao automatica do bloqueio
                // salva os logs na tabela
                Log log = new Log("LOGIN_DESBLOQUEADO", valores.getKey(),
                        "Bloqueio expirado, removido automaticamente",
                        LocalDateTime.now());
                logService.salvarLog(log);
            }
            return expirado;
        });
    }

}
