package com.daniel_p_d.extractor.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel_p_d.extractor.dto.response.AnaliseTendenciaResponse;
import com.daniel_p_d.extractor.model.AnaliseTendencia;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.repository.AnaliseTendenciaRepository;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
import com.daniel_p_d.extractor.repository.HabilidadeRepository;
import com.daniel_p_d.extractor.repository.VagaHabilidadeRepository;
import com.daniel_p_d.extractor.repository.VagaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class AnaliseTendenciaService {
    
    private final CategoriaRepository categoriaRepository;
    private final HabilidadeRepository habilidadeRepository;
    private final VagaRepository vagaRepository;
    private final VagaHabilidadeRepository vagaHabilidadeRepository;
    private final AnaliseTendenciaRepository analiseTendenciaRepository;

    @Transactional 
    public void calcularAnaliseDoMesAtual(){
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth());

        List<Categoria> categorias = categoriaRepository.findByAtivaTrue();

        for(Categoria categoria: categorias){
            calcularParaCategoria(categoria, inicioMes, fimMes);
        }
    }

    private void calcularParaCategoria(Categoria categoria, LocalDate inicioMes, LocalDate fimMes) {
        long totalVagasNoPeriodo = vagaRepository.countByCategoriaIdAndDataPublicacaoBetween(categoria.getId(), inicioMes, fimMes);

        if (totalVagasNoPeriodo == 0) {
            log.debug("Nenhuma vaga encontrada para categoria {} no período, pulando", categoria.getNome());
            return;
        }

        var contagens = vagaHabilidadeRepository.contarMencoesPorCategoriaEPeriodo(categoria.getId(), inicioMes, fimMes);

        for (var contagem : contagens) {
            Habilidade habilidade = habilidadeRepository.findById(contagem.getHabilidadeId()).orElse(null);
            if (habilidade == null) continue;

            BigDecimal percentual = BigDecimal.valueOf(contagem.getQuantidade())
                .divide(BigDecimal.valueOf(totalVagasNoPeriodo), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

            BigDecimal variacao = calcularVariacao(habilidade, categoria, inicioMes, percentual);

            salvarOuAtualizar(habilidade, categoria, inicioMes, contagem.getQuantidade().intValue(), percentual, variacao);
        }

        log.info("Análise calculada para categoria {}: {} habilidade(s) processada(s)",
            categoria.getNome(), contagens.size());
    }

    private BigDecimal calcularVariacao(Habilidade habilidade, Categoria categoria, LocalDate periodoAtual, BigDecimal percentualAtual) {
        LocalDate periodoAnterior = periodoAtual.minusMonths(1);

        return analiseTendenciaRepository
            .findByHabilidadeIdAndCategoriaIdAndPeriodoReferencia(habilidade.getId(), categoria.getId(), periodoAnterior)
            .map(analiseAnterior -> {
                BigDecimal percentualAnterior = analiseAnterior.getPercentualVagas();
                if (percentualAnterior == null || percentualAnterior.compareTo(BigDecimal.ZERO) == 0) {
                    return null;
                }
                return percentualAtual.subtract(percentualAnterior)
                    .divide(percentualAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            })
            .orElse(null);
    }

    private void salvarOuAtualizar(Habilidade habilidade, Categoria categoria, LocalDate periodo, int quantidade, BigDecimal percentual, BigDecimal variacao) {
        AnaliseTendencia analise = analiseTendenciaRepository
            .findByHabilidadeIdAndCategoriaIdAndPeriodoReferencia(habilidade.getId(), categoria.getId(), periodo)
            .orElse(AnaliseTendencia.builder()
                .habilidade(habilidade)
                .categoria(categoria)
                .periodoReferencia(periodo)
                .build());

        analise.setQuantidadeMencoes(quantidade);
        analise.setPercentualVagas(percentual);
        analise.setVariacaoPercentual(variacao);

        analiseTendenciaRepository.save(analise);
    }

    public List<AnaliseTendenciaResponse> consultarPorCategoriaEPeriodo(Long categoriaId, LocalDate periodo) {
        LocalDate inicioMes = periodo.with(TemporalAdjusters.firstDayOfMonth());

        return analiseTendenciaRepository
            .findByCategoriaIdAndPeriodoReferenciaOrderByQuantidadeMencoesDesc(categoriaId, inicioMes)
            .stream()
            .map(a -> new AnaliseTendenciaResponse(
                a.getHabilidade().getNome(),
                a.getQuantidadeMencoes(),
                a.getPercentualVagas(),
                a.getVariacaoPercentual()
            ))
            .toList();
    }
}
