package com.daniel_p_d.extractor.analysis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    private volatile List<PadraoBusca> padroesDeBusca = Collections.emptyList();

    private record PadraoBusca(Pattern pattern, Habilidade habilidade) {}

    // palavras para possiveis diferencias
    private static final List<String> PALAVRAS_DIFERENCIAL = List.of(
        "diferencial", "desejável", "desejavel", "será um plus", "plus", "bônus", "bonus"
    );

    @PostConstruct
    public void carregarListaDeSkills() {
        List<Habilidade> todasAsSkills = habilidadeRepository.findAll();

        List<PadraoBusca> novaLista = todasAsSkills.stream()
            .sorted(Comparator.comparingInt((Habilidade h) -> h.getNome().length()).reversed())
            .map(h -> new PadraoBusca(criarPadraoDeBusca(h.getNome()), h))
            .toList();

        this.padroesDeBusca = novaLista;
        log.info("Lista de skills carregada: {} habilidades", novaLista.size());
    }

     // recarrega a lista
    @Scheduled(fixedRateString = "${habilidades.cache.refresh-ms:3600000}")
    public void recarregarPeriodicamente() {
        carregarListaDeSkills();
    }

    // evitar erro na busca
    private Pattern criarPadraoDeBusca(String nomeDaSkill) {
        String nomeEscapado = Pattern.quote(nomeDaSkill);
        return Pattern.compile("\\b" + nomeEscapado + "\\b", Pattern.CASE_INSENSITIVE);
    }

    // extração das vagas normal
    public Set<Habilidade> extrairHabilidades(String descricaoDaVaga) {
        return extrairComDetalhes(descricaoDaVaga).stream()
            .map(HabilidadeDetectada::habilidade)
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    // extração das vagas normal com quantidade e diferencial
    public List<HabilidadeDetectada> extrairComDetalhes(String descricaoDaVaga) {
        if (descricaoDaVaga == null || descricaoDaVaga.isBlank()) {
            return Collections.emptyList();
        }

        List<HabilidadeDetectada> resultado = new ArrayList<>();

        for (PadraoBusca padrao : padroesDeBusca) {
            Matcher matcher = padrao.pattern().matcher(descricaoDaVaga);

            int quantidadeMencoes = 0;
            boolean pareceDiferencial = false;

            while (matcher.find()) {
                quantidadeMencoes++;

                // verifica o texto proximo a posição encontrada
                int inicioTrecho = Math.max(0, matcher.start() - 50);
                String trechoAoRedor = descricaoDaVaga.substring(inicioTrecho, matcher.start()).toLowerCase();

                boolean temPalavraDiferencial = PALAVRAS_DIFERENCIAL.stream()
                    .anyMatch(trechoAoRedor::contains);

                if (temPalavraDiferencial) {
                    pareceDiferencial = true;
                }
            }

            if (quantidadeMencoes > 0) {
                resultado.add(new HabilidadeDetectada(
                    padrao.habilidade(),
                    quantidadeMencoes,
                    pareceDiferencial
                ));
            }
        }

        return resultado;
    }
}