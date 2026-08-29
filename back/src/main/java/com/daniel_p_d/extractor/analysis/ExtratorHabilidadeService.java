package com.daniel_p_d.extractor.analysis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.analysis.config.MapaApelidosHabilidade;
import com.daniel_p_d.extractor.analysis.model.HabilidadeDetectada;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.repository.HabilidadeRepository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtratorHabilidadeService {

    private final HabilidadeRepository habilidadeRepository;
    private final MapaApelidosHabilidade mapaApelidos;

    private volatile List<PadraoBusca> padroesDeBusca = Collections.emptyList();

    private record PadraoBusca(Pattern pattern, Habilidade habilidade, String textoPadrao) {}

    private static final List<String> PALAVRAS_DIFERENCIAL = List.of(
        "diferencial", "desejável", "desejavel", "será um plus", "plus", "bônus", "bonus"
    );

    @PostConstruct
    public void carregarListaDeSkills() {
        List<Habilidade> todasAsSkills = habilidadeRepository.findAll();

        List<PadraoBusca> novaLista = new ArrayList<>();

        for (Habilidade h : todasAsSkills) {
            //nome oficial da habilidade
            novaLista.add(new PadraoBusca(criarPadraoDeBusca(h.getNome()), h, h.getNome()));

            // variacoes cadastrados no MapaApelidosHabilidade
            for (String apelido : mapaApelidos.apelidosPara(h.getNome())) {
                novaLista.add(new PadraoBusca(criarPadraoDeBusca(apelido), h, apelido));
            }
        }

        novaLista.sort(Comparator.comparingInt((PadraoBusca p) -> p.textoPadrao().length()).reversed());

        this.padroesDeBusca = novaLista;
        log.info("Lista de skills carregada: {} padrões de busca ({} habilidades)", novaLista.size(), todasAsSkills.size());
    }

    @Scheduled(fixedRateString = "${habilidades.cache.refresh-ms:3600000}")
    public void recarregarPeriodicamente() {
        carregarListaDeSkills();
    }

    private Pattern criarPadraoDeBusca(String texto) {
        String textoEscapado = Pattern.quote(texto);
        return Pattern.compile("\\b" + textoEscapado + "\\b", Pattern.CASE_INSENSITIVE);
    }

    public Set<Habilidade> extrairHabilidades(String descricaoDaVaga) {
        return extrairComDetalhes(descricaoDaVaga).stream()
            .map(HabilidadeDetectada::habilidade)
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    public List<HabilidadeDetectada> extrairComDetalhes(String descricaoDaVaga) {
        if (descricaoDaVaga == null || descricaoDaVaga.isBlank()) {
            return Collections.emptyList();
        }
        Map<Habilidade, Integer> contagemPorHabilidade = new LinkedHashMap<>();
        Map<Habilidade, Boolean> diferencialPorHabilidade = new HashMap<>();

        List<int[]> intervalosJaCasados = new ArrayList<>();

        for (PadraoBusca padrao : padroesDeBusca) {
            Matcher matcher = padrao.pattern().matcher(descricaoDaVaga);

            while (matcher.find()) {
                int inicio = matcher.start();
                int fim = matcher.end();

                if (sobrepoeIntervaloExistente(inicio, fim, intervalosJaCasados)) {
                    continue;
                }

                intervalosJaCasados.add(new int[]{inicio, fim});

                contagemPorHabilidade.merge(padrao.habilidade(), 1, Integer::sum);

                int inicioTrecho = Math.max(0, inicio - 50);
                String trechoAoRedor = descricaoDaVaga.substring(inicioTrecho, inicio).toLowerCase();
                boolean temPalavraDiferencial = PALAVRAS_DIFERENCIAL.stream().anyMatch(trechoAoRedor::contains);

                if (temPalavraDiferencial) {
                    diferencialPorHabilidade.put(padrao.habilidade(), true);
                }
            }
        }

        List<HabilidadeDetectada> resultado = new ArrayList<>();
        for (var entry : contagemPorHabilidade.entrySet()) {
            resultado.add(new HabilidadeDetectada(
                entry.getKey(),
                entry.getValue(),
                diferencialPorHabilidade.getOrDefault(entry.getKey(), false)
            ));
        }

        return resultado;
    }

    private boolean sobrepoeIntervaloExistente(int inicio, int fim, List<int[]> intervalos) {
        for (int[] intervalo : intervalos) {
            boolean sobrepoe = inicio < intervalo[1] && fim > intervalo[0];
            if (sobrepoe) return true;
        }
        return false;
    }
}