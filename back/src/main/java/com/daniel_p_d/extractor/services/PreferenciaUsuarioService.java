package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.CriarPreferenciaRequest;
import com.daniel_p_d.extractor.dto.response.PreferenciaUsuarioResponse;
import com.daniel_p_d.extractor.exception.CategoriaNaoEncontradaException;
import com.daniel_p_d.extractor.exception.PreferenciaInvalidaException;
import com.daniel_p_d.extractor.exception.PreferenciaJaCadastradaException;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.PreferenciaUsuario;
import com.daniel_p_d.extractor.model.Usuario;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
import com.daniel_p_d.extractor.repository.PreferenciaUsuarioRepository;
import com.daniel_p_d.extractor.util.TextoUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreferenciaUsuarioService {
    
    private static final int MAX_CATEGORIAS_ATIVAS = 2;

    private final PreferenciaUsuarioRepository preferenciaUsuarioRepository;
    private final CategoriaRepository categoriaRepository;

    // criar preferência do catálogo
    @Transactional
    public PreferenciaUsuarioResponse criar(Usuario usuario, CriarPreferenciaRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
            .filter(c -> Boolean.TRUE.equals(c.getAtiva()) && Boolean.TRUE.equals(c.getPadrao()))
            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada: " + request.categoriaId()));

        return ativarOuCriar(usuario, categoria);
    }

    // criar preferência personalizada.
    @Transactional
    public PreferenciaUsuarioResponse criarPersonalizada(Usuario usuario, String termoDigitado) {
        String nome = TextoUtil.nomeLimpo(termoDigitado);
        String slug = TextoUtil.paraSlug(nome);

        if (slug.length() < 3) {
            throw new PreferenciaInvalidaException("Informe um termo com pelo menos 3 letras.");
        }

        // reaproveita categoria existente, cria se for nova
        Categoria categoria = categoriaRepository.findByTermoBusca(slug)
            .or(() -> categoriaRepository.findByNomeIgnoreCase(nome))
            .orElseGet(() -> categoriaRepository.save(Categoria.builder()
                .nome(nome)
                .termoBusca(slug)
                .ativa(true)
                .padrao(false)
                .build()));

        if (!Boolean.TRUE.equals(categoria.getAtiva())) {
            throw new PreferenciaInvalidaException("Este termo não está disponível.");
        }

        return ativarOuCriar(usuario, categoria);
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

    private PreferenciaUsuarioResponse ativarOuCriar(Usuario usuario, Categoria categoria) {
        var existente = preferenciaUsuarioRepository.findByUsuarioIdAndCategoriaId(usuario.getId(), categoria.getId());

        if (existente.isPresent() && Boolean.TRUE.equals(existente.get().getAtiva())) {
            throw new PreferenciaJaCadastradaException("Preferência já cadastrada para essa categoria");
        }

        // limite de uso
        if (preferenciaUsuarioRepository.contarAtivas(usuario.getId()) >= MAX_CATEGORIAS_ATIVAS) {
            throw new PreferenciaInvalidaException("Você já acompanha " + MAX_CATEGORIAS_ATIVAS
                + " categorias. Remova uma para adicionar outra.");
        }

        if (existente.isPresent()) {
            PreferenciaUsuario p = existente.get();
            p.setAtiva(true);
            return paraResponse(preferenciaUsuarioRepository.save(p));
        }

        PreferenciaUsuario preferencia = PreferenciaUsuario.builder()
            .usuario(usuario)
            .categoria(categoria)
            .ativa(true)
            .build();

        return paraResponse(preferenciaUsuarioRepository.save(preferencia));
    }

    private PreferenciaUsuarioResponse paraResponse(PreferenciaUsuario preferencia) {
        return new PreferenciaUsuarioResponse(
            preferencia.getId(),
            preferencia.getCategoria().getId(),
            preferencia.getCategoria().getNome(),
            preferencia.getAtiva()
        );
    }
}
