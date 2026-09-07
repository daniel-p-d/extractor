package com.daniel_p_d.extractor.scrapper.portais;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.scrapper.ScraperClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CathoScraper implements ScraperClient{

    @Value("${scraper.catho.delay-ms:600}")
    private long delayEntreRequestsMs;

    private static final String DOMINIO = "https://www.catho.com.br";
    private static final String URL_BUSCA = DOMINIO + "/vagas/desenvolvedor/";
    private static final String URL_API_DETALHE = "https://oferta.catho.com.br/offer/%s/d/j?ipo=42&iapo=1";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String nomePortal() {
        return "Catho";
    }

    @Override
    public List<VagaColetada> coletar(Categoria categoria) {
        List<VagaColetada> resultado = new ArrayList<>();

        String urlBusca = DOMINIO + "/vagas/" + categoria.getTermoBusca() + "/";

        List<String> idsDeVagas = coletarIdsDaListagem(urlBusca);
        log.info("Catho [{}]: {} IDs de vaga encontrados na listagem", categoria.getNome(), idsDeVagas.size());

        for (String id : idsDeVagas) {
            try {
                VagaColetada vaga = buscarDetalheDaVaga(id);
                if (vaga != null) {
                    resultado.add(vaga);
                }
                aguardarEntreRequests();
            } catch (Exception e) {
                log.warn("Erro ao buscar detalhe da vaga {}: {}", id, e.getMessage());
            }
        }

        return resultado;
    }

    private List<String> coletarIdsDaListagem(String urlBusca) {
        List<String> ids = new ArrayList<>();

        try {
            Document pagina = Jsoup.connect(urlBusca)
                .userAgent(USER_AGENT)
                .timeout(15000)
                .get();

            Elements cards = pagina.select("li[data-offer-item]");
            for (Element card : cards) {
                String id = card.attr("data-offer-item");
                if (!id.isBlank()) {
                    ids.add(id);
                }
            }

        } catch (Exception e) {
            log.error("Erro ao coletar listagem do Catho: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na coleta da listagem do Catho", e);
        }

        return ids;
    }


    private VagaColetada buscarDetalheDaVaga(String idVaga) throws Exception {
        String url = String.format(URL_API_DETALHE, idVaga);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(15))
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.warn("Catho API retornou status {} para vaga {}", response.statusCode(), idVaga);
            return null;
        }

        CathoOfferDetailResponse detalhe = parsearResposta(response.body());
        return converterParaVagaColetada(detalhe, idVaga);
    }

    private CathoOfferDetailResponse parsearResposta(String corpoResposta) throws Exception {
        String corpoTrimado = corpoResposta.trim();

        if (corpoTrimado.startsWith("\"")) {
            String jsonReal = objectMapper.readValue(corpoTrimado, String.class);
            return objectMapper.readValue(jsonReal, CathoOfferDetailResponse.class);
        }
        return objectMapper.readValue(corpoTrimado, CathoOfferDetailResponse.class);
    }

    private VagaColetada converterParaVagaColetada(CathoOfferDetailResponse detalhe, String idVaga) {
        var oferta = detalhe.getO();
        var empresa = detalhe.getC();

        if (oferta == null || oferta.getTitulo() == null) {
            log.warn("Vaga {} sem dados de oferta válidos", idVaga);
            return null;
        }

        String urlCompleta = DOMINIO + "/vagas" + oferta.getUrlRelativa();

        String localizacao = (oferta.getLocalizacoes() != null && !oferta.getLocalizacoes().isEmpty())
            ? String.join(", ", oferta.getLocalizacoes())
            : null;

        LocalDate dataPublicacao = parseDataPublicacao(oferta.getDataPublicacao());

        BigDecimal salarioMin = converterSalario(oferta.getSalarioMin());
        BigDecimal salarioMax = converterSalario(oferta.getSalarioMax());

        return new VagaColetada(
            oferta.getTitulo(),
            urlCompleta,
            oferta.getDescricao(),
            empresa != null ? empresa.getNome() : null,
            localizacao,
            oferta.getModalidade(),
            oferta.getTipoContrato(),
            oferta.getTitulo(), // senioridade
            salarioMin,
            salarioMax,
            dataPublicacao
        );
    }

    private BigDecimal converterSalario(Double valor) {
        if (valor == null || valor == 0.0) {
            return null;
        }
        return BigDecimal.valueOf(valor);
    }

    private LocalDate parseDataPublicacao(String dataIso) {
        if (dataIso == null || dataIso.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDateTime.parse(dataIso, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
        } catch (Exception e) {
            log.warn("Não foi possível parsear data '{}', usando data atual", dataIso);
            return LocalDate.now();
        }
    }

    private void aguardarEntreRequests() {
        try {
            Thread.sleep(delayEntreRequestsMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
