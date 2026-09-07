package com.daniel_p_d.extractor.scrapper.portais;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class VagasComScraper implements ScraperClient{

    @Value("${scraper.vagascom.delay-ms:600}")
    private long delayEntreRequestsMs;

    private static final String DOMINIO = "https://www.vagas.com.br";
    private static final String URL_BUSCA = DOMINIO + "/vagas-de-desenvolvedor";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    private static final Pattern PADRAO_HA_DIAS = Pattern.compile("Há (\\d+) dias?");
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private record VagaParcial(String id, String titulo, String url, String empresa, String nivel) {}

    @Override
    public String nomePortal() {
        return "Vagas.com";
    }

    @Override
    public List<VagaColetada> coletar(Categoria categoria) {
        List<VagaColetada> resultado = new ArrayList<>();

        String urlBusca = DOMINIO + "/vagas-de-" + categoria.getTermoBusca();

        try {
            Document pagina = Jsoup.connect(urlBusca)
                .userAgent(USER_AGENT)
                .timeout(15000)
                .get();

            Elements cards = pagina.select("li.vaga");
            log.info("Vagas.com [{}]: {} vagas encontradas na página de listagem", categoria.getNome(), cards.size());

            for (Element card : cards) {
                try {
                    VagaColetada vaga = extrairVagaDoCard(card);
                    if (vaga != null) {
                        resultado.add(vaga);
                    }
                } catch (Exception e) {
                    log.warn("Erro ao processar card de vaga, pulando: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Erro ao coletar Vagas.com: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na coleta do Vagas.com", e);
        }

        return resultado;
    }

    private VagaColetada extrairVagaDoCard(Element card) {
        Element link = card.selectFirst("h2.cargo a.link-detalhes-vaga");
        if (link == null) return null;

        String urlRelativa = link.attr("href");
        String titulo = link.text().trim();

        if (urlRelativa.isBlank() || titulo.isBlank()) {
            return null;
        }

        String url = DOMINIO + urlRelativa;
        String empresa = textoOuNull(card, "span.emprVaga");
        String nivel = textoOuNull(card, "div.nivelQtdVagas span.nivelVaga");
        String descricaoResumida = textoOuNull(card, "div.detalhes p");
        String localizacao = extrairLocalizacao(card);
        LocalDate dataPublicacao = extrairDataPublicacao(card);

        return new VagaColetada(
            titulo,
            url,
            descricaoResumida,
            empresa,
            localizacao,
            localizacao,
            null,  
            nivel != null ? nivel : titulo,
            null,
            null,
            dataPublicacao
        );
    }

    private String extrairLocalizacao(Element card) {
        Element divOuSpan = card.selectFirst("footer .vaga-local");
        if (divOuSpan == null) return null;

        Element clone = divOuSpan.clone();
        clone.select("div.tooltip-place").remove();
        clone.select("i").remove(); 
        return clone.text().trim();
    }

    private LocalDate extrairDataPublicacao(Element card) {
        Element spanData = card.selectFirst("span.data-publicacao");
        if (spanData == null) return LocalDate.now();

        String texto = spanData.text().trim();

        Matcher matcher = PADRAO_HA_DIAS.matcher(texto);
        if (matcher.find()) {
            int dias = Integer.parseInt(matcher.group(1));
            return LocalDate.now().minusDays(dias);
        }

        try {
            return LocalDate.parse(texto, FORMATO_DATA_BR);
        } catch (Exception e) {
            log.warn("Não foi possível parsear data '{}', usando data atual", texto);
            return LocalDate.now();
        }
    }

    private String textoOuNull(Element base, String seletor) {
        Element elemento = base.selectFirst(seletor);
        return elemento != null ? elemento.text().trim() : null;
    }
}
