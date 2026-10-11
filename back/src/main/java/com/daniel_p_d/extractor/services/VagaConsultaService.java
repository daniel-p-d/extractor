package com.daniel_p_d.extractor.services;

import com.daniel_p_d.extractor.dto.response.PaginaResponse;
import com.daniel_p_d.extractor.dto.response.VagaResponse;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;
import com.daniel_p_d.extractor.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VagaConsultaService {

    private final VagaRepository vagaRepository;

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
            v.getHabilidades().stream().map(vh -> vh.getHabilidade().getNome()).toList()
        );
    }
}
