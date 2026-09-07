package com.daniel_p_d.extractor.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.repository.VagaHabilidadeRepository;
import com.daniel_p_d.extractor.repository.VagaRepository;

import jakarta.transaction.Transactional;

import com.daniel_p_d.extractor.analysis.ExtratorHabilidadeService;
import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.Empresa;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.VagaHabilidade;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VagaService {
    
    private final VagaRepository vagaRepository;
    private final VagaHabilidadeRepository vagaHabilidadeRepository;
    private final EmpresaService empresaService;
    private final ConversorEnumService conversorEnumService;
    private final ExtratorHabilidadeService extratorHabilidadeService;

    public record ResultadoProcessamento(boolean novaVaga, Vaga vaga) {}

    @Transactional
    public ResultadoProcessamento processarVagaColetada(VagaColetada dados, Portal portal, Categoria categoria) {

        if (vagaRepository.existsByUrl(dados.url())) {
            Vaga existente = vagaRepository.findByUrl(dados.url()).orElseThrow();
            return new ResultadoProcessamento(false, existente);
        }

        Empresa empresa = empresaService.resolverOuCriar(dados.nomeEmpresa());

        Vaga vaga = Vaga.builder()
            .titulo(dados.titulo())
            .url(dados.url())
            .descricao(dados.descricaoBruta())
            .portal(portal)
            .empresa(empresa)
            .categoria(categoria)
            .localizacao(dados.localizacao())
            .modalidade(conversorEnumService.converterModalidade(dados.modalidadeTexto()))
            .regime(conversorEnumService.converterRegime(dados.regimeTexto()))
            .senioridade(conversorEnumService.converterSenioridade(dados.senioridadeTexto()))
            .salarioMin(dados.salarioMin())
            .salarioMax(dados.salarioMax())
            .dataPublicacao(dados.dataPublicacao())
            .status(StatusVaga.ATIVA)
            .build();

        vaga = vagaRepository.save(vaga);
        associarHabilidades(vaga);

        return new ResultadoProcessamento(true, vaga);
    }

    private void associarHabilidades(Vaga vaga) {
        Set<Habilidade> habilidadesEncontradas = extratorHabilidadeService.extrairHabilidades(vaga.getDescricao());

        for (Habilidade habilidade : habilidadesEncontradas) {
            VagaHabilidade vagaHabilidade = VagaHabilidade.builder()
                .vaga(vaga)
                .habilidade(habilidade)
                .obrigatoria(true)
                .build();

            vagaHabilidadeRepository.save(vagaHabilidade);
        }

        log.debug("{} habilidades associadas à vaga {}", habilidadesEncontradas.size(), vaga.getId());
    }
}
