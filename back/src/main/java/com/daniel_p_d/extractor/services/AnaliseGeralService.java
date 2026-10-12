package com.daniel_p_d.extractor.services;

import com.daniel_p_d.extractor.dto.response.ResumoAnaliseResponse;
import com.daniel_p_d.extractor.dto.response.ResumoAnaliseResponse.*;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.Enums.NivelSenioridade;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;
import com.daniel_p_d.extractor.exception.CategoriaNaoEncontradaException;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
import com.daniel_p_d.extractor.repository.VagaHabilidadeRepository;
import com.daniel_p_d.extractor.repository.VagaRepository;
import com.daniel_p_d.extractor.util.NormalizadorLocalizacao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnaliseGeralService {

    private static final List<NivelSenioridade> ORDEM = List.of(
        NivelSenioridade.ESTAGIO, NivelSenioridade.JUNIOR, NivelSenioridade.PLENO,
        NivelSenioridade.SENIOR, NivelSenioridade.ESPECIALISTA, NivelSenioridade.OUTRO);

    private final VagaRepository vagaRepository;
    private final VagaHabilidadeRepository vagaHabilidadeRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public ResumoAnaliseResponse resumo(Long categoriaId, int dias) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada: " + categoriaId));

        int janela = Math.max(1, Math.min(dias, 365));
        LocalDateTime ate = LocalDateTime.now();
        LocalDateTime desde = ate.minusDays(janela);
        LocalDateTime desdeAnterior = desde.minusDays(janela);
        StatusVaga st = StatusVaga.ATIVA;

        long total = vagaRepository.contarNoPeriodo(st, categoriaId, desde, ate);
        long totalAnterior = vagaRepository.contarNoPeriodo(st, categoriaId, desdeAnterior, desde);

        // vagas por senioridade
        Map<NivelSenioridade, Long> porSen = new EnumMap<>(NivelSenioridade.class);
        for (Object[] r : vagaRepository.contarPorSenioridade(st, categoriaId, desde, ate)) {
            porSen.merge(nivel(r[0]), n(r[1]), Long::sum);
        }
        List<Item> senioridade = ORDEM.stream().filter(porSen::containsKey)
            .map(nv -> new Item(rotulo(nv), porSen.get(nv), pct(porSen.get(nv), total))).toList();

        // vagas por portal
        List<Item> porPortal = vagaRepository.contarPorPortal(st, categoriaId, desde, ate).stream()
            .map(r -> new Item((String) r[0], n(r[1]), pct(n(r[1]), total))).toList();

        // vagas por localização - UF
        Map<String, Long> porUf = new HashMap<>();
        for (Object[] r : vagaRepository.contarPorLocalizacao(st, categoriaId, desde, ate)) {
            porUf.merge(NormalizadorLocalizacao.paraUf((String) r[0]), n(r[1]), Long::sum);
        }
        List<Item> localizacao = porUf.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
            .map(e -> new Item(e.getKey(), e.getValue(), pct(e.getValue(), total))).toList();

        // empresas com mais vagas
        List<Item> empresas = vagaRepository.topEmpresas(st, categoriaId, desde, ate, PageRequest.of(0, 10)).stream()
            .map(r -> new Item((String) r[0], n(r[1]), pct(n(r[1]), total))).toList();

        // tecnologias em destaque - participação atual e variação
        Map<String, Long> atuais = paraMapa(vagaHabilidadeRepository.contarHabilidades(st, categoriaId, desde, ate));
        Map<String, Long> anteriores = paraMapa(vagaHabilidadeRepository.contarHabilidades(st, categoriaId, desdeAnterior, desde));
        List<Tecnologia> tecnologias = atuais.entrySet().stream().limit(10).map(e -> {
            BigDecimal p = pct(e.getValue(), total);
            BigDecimal variacao = totalAnterior == 0 ? null
                : p.subtract(pct(anteriores.getOrDefault(e.getKey(), 0L), totalAnterior));
            return new Tecnologia(e.getKey(), e.getValue(), p, variacao);
        }).toList();

        // habilidades por senioridade
        Map<NivelSenioridade, Map<String, Long>> habPorSen = new EnumMap<>(NivelSenioridade.class);
        for (Object[] r : vagaHabilidadeRepository.contarHabilidadesPorSenioridade(st, categoriaId, desde, ate)) {
            habPorSen.computeIfAbsent(nivel(r[0]), k -> new LinkedHashMap<>()).merge((String) r[1], n(r[2]), Long::sum);
        }
        List<GrupoHabilidades> grupos = ORDEM.stream().filter(habPorSen::containsKey).map(nv -> {
            long totalNivel = porSen.getOrDefault(nv, 0L);
            List<Item> itens = habPorSen.get(nv).entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5)
                .map(e -> new Item(e.getKey(), e.getValue(), pct(e.getValue(), totalNivel))).toList();
            return new GrupoHabilidades(rotulo(nv), totalNivel, itens);
        }).toList();

        return new ResumoAnaliseResponse(categoria.getNome(), janela, total,
            volume(categoriaId, desde, ate, total, totalAnterior),
            porPortal, senioridade, localizacao, empresas, tecnologias, grupos);
    }

    // variação do número de vagas coletadas + série diária
    private Volume volume(Long categoriaId, LocalDateTime desde, LocalDateTime ate, long atual, long anterior) {
        BigDecimal variacao = anterior == 0 ? null
            : BigDecimal.valueOf((atual - anterior) * 100.0 / anterior).setScale(2, RoundingMode.HALF_UP);

        Map<LocalDate, Long> porDia = new TreeMap<>();
        for (LocalDate d = desde.toLocalDate(); !d.isAfter(ate.toLocalDate()); d = d.plusDays(1)) {
            porDia.put(d, 0L);
        }
        for (LocalDateTime t : vagaRepository.listarExtracoes(StatusVaga.ATIVA, categoriaId, desde, ate)) {
            porDia.merge(t.toLocalDate(), 1L, Long::sum);
        }
        List<Ponto> serie = porDia.entrySet().stream().map(e -> new Ponto(e.getKey(), e.getValue())).toList();
        return new Volume(atual, anterior, variacao, serie);
    }

    private Map<String, Long> paraMapa(List<Object[]> linhas) {
        Map<String, Long> m = new LinkedHashMap<>();
        for (Object[] r : linhas) m.put((String) r[0], n(r[1]));
        return m;
    }

    private long n(Object o) { return ((Number) o).longValue(); }

    private NivelSenioridade nivel(Object o) { return o == null ? NivelSenioridade.OUTRO : (NivelSenioridade) o; }

    private BigDecimal pct(long parte, long total) {
        if (total == 0) return BigDecimal.ZERO.setScale(2);
        return BigDecimal.valueOf(parte * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
    }

    private String rotulo(NivelSenioridade nv) {
        return switch (nv) {
            case ESTAGIO -> "Estágio";
            case JUNIOR -> "Júnior";
            case PLENO -> "Pleno";
            case SENIOR -> "Sênior";
            case ESPECIALISTA -> "Especialista";
            case OUTRO -> "Não informada";
        };
    }
}