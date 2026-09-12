package com.daniel_p_d.extractor.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.PreferenciaUsuario;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.repository.PreferenciaUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {
    private final PreferenciaUsuarioRepository preferenciaUsuarioRepository;
    private final EmailService emailService;

    @Transactional
    public void notificarSobreVagasNovas(List<Vaga> vagasNovas) {
        if (vagasNovas.isEmpty()) {
            return;
        }

        List<Vaga> vagasComCategoria = vagasNovas.stream()
            .filter(v -> v.getCategoria() != null)
            .toList();

        if (vagasComCategoria.isEmpty()) {
            log.debug("Nenhuma vaga nova com categoria definida.");
            return;
        }

        Set<Long> categoriaIds = vagasComCategoria.stream()
            .map(v -> v.getCategoria().getId())
            .collect(Collectors.toSet());

        List<PreferenciaUsuario> preferenciasRelevantes =
            preferenciaUsuarioRepository.findByAtivaTrueAndCategoriaIdIn(new ArrayList<>(categoriaIds));

        if (preferenciasRelevantes.isEmpty()) {
            log.debug("Nenhuma preferência ativa bateu com as categorias desta coleta");
            return;
        }

        Map<Long, List<PreferenciaUsuario>> preferenciasPorUsuario = preferenciasRelevantes.stream()
            .collect(Collectors.groupingBy(p -> p.getUsuario().getId()));

        for (List<PreferenciaUsuario> preferenciasDoUsuario : preferenciasPorUsuario.values()) {
            notificarUsuario(preferenciasDoUsuario, vagasComCategoria);
        }
    }

    private void notificarUsuario(List<PreferenciaUsuario> preferenciasDoUsuario, List<Vaga> vagasNovas) {
        var usuario = preferenciasDoUsuario.get(0).getUsuario();

        Set<Long> categoriasDoUsuario = preferenciasDoUsuario.stream()
            .map(p -> p.getCategoria().getId())
            .collect(Collectors.toSet());

        List<Vaga> vagasRelevantes = vagasNovas.stream()
            .filter(v -> categoriasDoUsuario.contains(v.getCategoria().getId()))
            .toList();

        if (vagasRelevantes.isEmpty()) {
            return;
        }

        String corpo = montarCorpoEmail(vagasRelevantes);
        emailService.enviar(usuario.getLogin(), "Novas vagas encontradas para você", corpo);
    }

    private String montarCorpoEmail(List<Vaga> vagas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Encontramos ").append(vagas.size())
            .append(" vaga(s) nova(s) na(s) categoria(s) que você acompanha:\n\n");

        for (Vaga vaga : vagas) {
            sb.append("- ").append(vaga.getTitulo());
            if (vaga.getEmpresa() != null) {
                sb.append(" (").append(vaga.getEmpresa().getNome()).append(")");
            }
            sb.append(" [").append(vaga.getCategoria().getNome()).append("]");
            sb.append("\n  ").append(vaga.getUrl()).append("\n\n");
        }

        return sb.toString();
    }

}
