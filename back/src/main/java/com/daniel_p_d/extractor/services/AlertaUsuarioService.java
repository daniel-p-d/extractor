package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.CriarAlertaRequest;
import com.daniel_p_d.extractor.dto.response.AlertaUsuarioResponse;
import com.daniel_p_d.extractor.exception.AlertaJaCadastradoException;
import com.daniel_p_d.extractor.exception.HabilidadeNaoEncontradaException;
import com.daniel_p_d.extractor.model.AlertaUsuario;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.model.Usuario;
import com.daniel_p_d.extractor.repository.AlertaUsuarioRepository;
import com.daniel_p_d.extractor.repository.HabilidadeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertaUsuarioService {
    private final AlertaUsuarioRepository alertaUsuarioRepository;
    private final HabilidadeRepository habilidadeRepository;

    @Transactional
    public AlertaUsuarioResponse criar(Usuario usuario, CriarAlertaRequest request) {
        Habilidade habilidade = habilidadeRepository.findById(request.habilidadeId())
            .orElseThrow(() -> new HabilidadeNaoEncontradaException("Habilidade não encontrada: " + request.habilidadeId()));

        if (alertaUsuarioRepository.existsByUsuarioIdAndHabilidadeId(usuario.getId(), habilidade.getId())) {
            throw new AlertaJaCadastradoException("Alerta já cadastrado para essa habilidade");
        }

        AlertaUsuario alerta = AlertaUsuario.builder()
            .usuario(usuario)
            .habilidade(habilidade)
            .ativo(true)
            .build();

        alerta = alertaUsuarioRepository.save(alerta);

        return paraResponse(alerta);
    }

    public List<AlertaUsuarioResponse> listarPorUsuario(Long usuarioId) {
        return alertaUsuarioRepository.findByUsuarioId(usuarioId).stream()
            .map(this::paraResponse)
            .toList();
    }

    @Transactional
    public void desativar(Long alertaId, Long usuarioId) {
        AlertaUsuario alerta = alertaUsuarioRepository.findById(alertaId)
            .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado"));

        if (!alerta.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("Alerta não pertence ao usuário");
        }

        alerta.setAtivo(false);
        alertaUsuarioRepository.save(alerta);
    }

    private AlertaUsuarioResponse paraResponse(AlertaUsuario alerta) {
        return new AlertaUsuarioResponse(
            alerta.getId(),
            alerta.getHabilidade().getNome(),
            alerta.getAtivo(),
            alerta.getCriadoEm(),
            alerta.getUltimaNotificacaoEm()
        );
    }
}
