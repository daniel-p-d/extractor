package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.CriarPreferenciaRequest;
import com.daniel_p_d.extractor.dto.response.PreferenciaUsuarioResponse;
import com.daniel_p_d.extractor.exception.CategoriaNaoEncontradaException;
import com.daniel_p_d.extractor.exception.PreferenciaJaCadastradaException;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.PreferenciaUsuario;
import com.daniel_p_d.extractor.model.Usuario;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
import com.daniel_p_d.extractor.repository.PreferenciaUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreferenciaUsuarioService {
    private final PreferenciaUsuarioRepository preferenciaUsuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public PreferenciaUsuarioResponse criar(Usuario usuario, CriarPreferenciaRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada: " + request.categoriaId()));

        if (preferenciaUsuarioRepository.existsByUsuarioIdAndCategoriaId(usuario.getId(), categoria.getId())) {
            throw new PreferenciaJaCadastradaException("Preferência já cadastrada para essa categoria");
        }

        PreferenciaUsuario preferencia = PreferenciaUsuario.builder()
            .usuario(usuario)
            .categoria(categoria)
            .ativa(true)
            .build();

        preferencia = preferenciaUsuarioRepository.save(preferencia);

        return paraResponse(preferencia);
    }

    public List<PreferenciaUsuarioResponse> listarPorUsuario(Long usuarioId) {
        return preferenciaUsuarioRepository.findByUsuarioId(usuarioId).stream()
            .map(this::paraResponse)
            .toList();
    }

    @Transactional
    public void desativar(Long preferenciaId, Long usuarioId) {
        PreferenciaUsuario preferencia = preferenciaUsuarioRepository.findById(preferenciaId)
            .orElseThrow(() -> new IllegalArgumentException("Preferência não encontrada"));

        if (!preferencia.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("Preferência não pertence ao usuário");
        }

        preferencia.setAtiva(false);
        preferenciaUsuarioRepository.save(preferencia);
    }

    private PreferenciaUsuarioResponse paraResponse(PreferenciaUsuario preferencia) {
        return new PreferenciaUsuarioResponse(
            preferencia.getId(),
            preferencia.getCategoria().getNome(),
            preferencia.getAtiva()
        );
    }
}
