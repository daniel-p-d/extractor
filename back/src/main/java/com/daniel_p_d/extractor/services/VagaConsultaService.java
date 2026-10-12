package com.daniel_p_d.extractor.services;

import com.daniel_p_d.extractor.dto.response.PaginaResponse;
import com.daniel_p_d.extractor.dto.response.VagaResponse;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;
import com.daniel_p_d.extractor.repository.PreferenciaUsuarioRepository;
import com.daniel_p_d.extractor.repository.VagaRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VagaConsultaService {

    private final VagaRepository vagaRepository;
    private final PreferenciaUsuarioRepository preferenciaUsuarioRepository;

    @Transactional(readOnly = true)
    public PaginaResponse<VagaResponse> listar(Long categoriaId, int pagina, int tamanho) {
        PageRequest pageable = PageRequest.of(pagina, Math.min(tamanho, 50),
            Sort.by(Sort.Direction.DESC, "dataPublicacao"));

        Page<Vaga> resultado = (categoriaId != null)
            ? vagaRepository.findByStatusAndCategoriaId(StatusVaga.ATIVA, categoriaId, pageable)
            : vagaRepository.findByStatus(StatusVaga.ATIVA, pageable);

        return new PaginaResponse<>(
            resultado.getContent().stream().map(this::paraResponse).toList(),
            resultado.getNumber(),
            resultado.getTotalPages(),
            resultado.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public PaginaResponse<VagaResponse> listar(Long usuarioId, Long categoriaId, LocalDate desde, int pagina, int tamanho) {
        List<Long> ids = idsPermitidos(usuarioId, categoriaId);
        if (ids.isEmpty()) {
            return new PaginaResponse<>(List.of(), 0, 0, 0);
        }
        LocalDateTime inicio = (desde != null) ? desde.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        PageRequest pageable = PageRequest.of(pagina, Math.min(tamanho, 50),
            Sort.by(Sort.Direction.DESC, "dataPublicacao"));

        Page<Vaga> resultado = vagaRepository.buscarPorCategoriasDesde(StatusVaga.ATIVA, ids, inicio, pageable);
        return new PaginaResponse<>(
            resultado.getContent().stream().map(this::paraResponse).toList(),
            resultado.getNumber(), resultado.getTotalPages(), resultado.getTotalElements());
    }

    @Transactional(readOnly = true)
    public LocalDate ultimaExtracao(Long usuarioId) {
        List<Long> ids = idsPermitidos(usuarioId, null);
        if (ids.isEmpty()) return null;
        LocalDateTime ultima = vagaRepository.ultimaExtracao(StatusVaga.ATIVA, ids);
        return ultima != null ? ultima.toLocalDate() : null;
    }

    // só categorias que o usuário acompanha
    private List<Long> idsPermitidos(Long usuarioId, Long categoriaId) {
        List<Long> minhas = preferenciaUsuarioRepository.findCategoriaIdsAtivas(usuarioId);
        if (categoriaId == null) return minhas;
        return minhas.contains(categoriaId) ? List.of(categoriaId) : List.of();
    }

    private VagaResponse paraResponse(Vaga v) {
        return new VagaResponse(
            v.getId(),
            v.getTitulo(),
            v.getUrl(),
            v.getEmpresa() != null ? v.getEmpresa().getNome() : null,
            v.getPortal().getNome(),
            v.getCategoria() != null ? v.getCategoria().getNome() : null,
            v.getLocalizacao(),
            v.getModalidade() != null ? v.getModalidade().name() : null,
            v.getSenioridade() != null ? v.getSenioridade().name() : null,
            v.getRegime() != null ? v.getRegime().name() : null,
            v.getDataPublicacao(),
            v.getExtraidoEm(),
            v.getHabilidades().stream().map(vh -> vh.getHabilidade().getNome()).toList()
        );
    }
}
