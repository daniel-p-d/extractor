package com.daniel_p_d.extractor.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.AlertaUsuario;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.repository.AlertaUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {
    
    private final AlertaUsuarioRepository alertaUsuarioRepository;
    private final EmailService emailService;

    @Transactional
    public void notificarSobreVagasNovas(Map<Vaga, Set<Habilidade>> vagasComHabilidades) {
        if (vagasComHabilidades.isEmpty()) {
            return;
        }

        Set<Long> habilidadeIdsEncontradas = vagasComHabilidades.values().stream()
            .flatMap(Set::stream)
            .map(Habilidade::getId)
            .collect(Collectors.toSet());

        if (habilidadeIdsEncontradas.isEmpty()) {
            return;
        }

        List<AlertaUsuario> alertasRelevantes = alertaUsuarioRepository
            .findByAtivoTrueAndHabilidadeIdIn(new ArrayList<>(habilidadeIdsEncontradas));

        if (alertasRelevantes.isEmpty()) {
            log.debug("Nenhum alerta ativo bateu com as habilidades desta coleta");
            return;
        }

        Map<Long, List<AlertaUsuario>> alertasPorUsuario = alertasRelevantes.stream()
            .collect(Collectors.groupingBy(a -> a.getUsuario().getId()));

        for (List<AlertaUsuario> alertasDoUsuario : alertasPorUsuario.values()) {
            notificarUsuario(alertasDoUsuario, vagasComHabilidades);
        }
    }

    private void notificarUsuario(List<AlertaUsuario> alertasDoUsuario, Map<Vaga, Set<Habilidade>> vagasComHabilidades) {
        var usuario = alertasDoUsuario.get(0).getUsuario();

        Set<Long> habilidadeIdsDoUsuario = alertasDoUsuario.stream()
            .map(a -> a.getHabilidade().getId())
            .collect(Collectors.toSet());

        List<Vaga> vagasRelevantes = vagasComHabilidades.entrySet().stream()
            .filter(entry -> entry.getValue().stream().anyMatch(h -> habilidadeIdsDoUsuario.contains(h.getId())))
            .map(Map.Entry::getKey)
            .toList();

        if (vagasRelevantes.isEmpty()) {
            return;
        }

        String corpo = montarCorpoEmail(vagasRelevantes);
        emailService.enviar(usuario.getLogin(), "Novas vagas encontradas para você", corpo);

        LocalDateTime agora = LocalDateTime.now();
        for (AlertaUsuario alerta : alertasDoUsuario) {
            alerta.setUltimaNotificacaoEm(agora);
        }
        alertaUsuarioRepository.saveAll(alertasDoUsuario);
    }

    private String montarCorpoEmail(List<Vaga> vagas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Encontramos ").append(vagas.size()).append(" vaga(s) nova(s) que combinam com seus alertas:\n\n");

        for (Vaga vaga : vagas) {
            sb.append("- ").append(vaga.getTitulo());
            if (vaga.getEmpresa() != null) {
                sb.append(" (").append(vaga.getEmpresa().getNome()).append(")");
            }
            sb.append("\n  ").append(vaga.getUrl()).append("\n\n");
        }

        return sb.toString();
    }

}
