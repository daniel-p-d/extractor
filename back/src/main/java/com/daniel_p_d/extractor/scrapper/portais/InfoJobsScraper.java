package com.daniel_p_d.extractor.scrapper.portais;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.config.JsoupConnector;
import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.scrapper.ScraperClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InfoJobsScraper implements ScraperClient{
    
    private final JsoupConnector jsoupConnector;
    private static final String DOMINIO = "https://www.infojobs.com.br";
    private static final String URL_BUSCA = DOMINIO + "/empregos.aspx?palabra=java";
    private static final DateTimeFormatter FORMATO_DATA_INFOJOBS = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public String nomePortal() {
        return "InfoJobs";
    }

    @Override
    public List<VagaColetada> coletar() {
        List<VagaColetada> vagasColetadas = new ArrayList<>();

        try {
            Document pagina = jsoupConnector.buscarDocumento(URL_BUSCA);
            Elements cards = pagina.select("div.js_rowCard");

            log.info("InfoJobs: {} vagas encontradas na página de listagem", cards.size());

            for (Element card : cards){
                try {
                    VagaColetada vaga = extrairVagaDoCard(card);
                    if (vaga != null){
                        vagasColetadas.add(vaga);
                    }
                } catch (Exception e) {
                    log.warn("Erro ao processar card de vaga individual, pulando: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Erro ao coletar vagas do InfoJobs: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na coleta do InfoJobs", e);
        }

        return vagasColetadas;
    }

    private VagaColetada extrairVagaDoCard(Element card) {
        String titulo = textoOuNull(card, "h2.js_vacancyTitle");

        Element elementoClicavel = card.selectFirst("div.js_vacancyLoad");
        String urlRelativa = elementoClicavel != null ? elementoClicavel.attr("data-href") : null;

        if (titulo == null || urlRelativa == null || urlRelativa.isBlank()) {
            log.warn("Card sem título ou URL, ignorando");
            return null;
        }

        String url = urlRelativa.startsWith("http") ? urlRelativa : DOMINIO + urlRelativa;

        String nomeEmpresa = textoOuNull(card, "a.text-body.text-decoration-none");

        String localizacao = textoOuNull(card, "div[class=mb-8]");

        String modalidadeTexto = textoDoIcone(card, "icon-house-and-building");
        String experienciaTexto = textoDoIcone(card, "icon-suitcase");

        String senioridadeTexto = titulo;

        LocalDate dataPublicacao = extrairDataPublicacao(card);

        String descricaoBruta = extrairDescricaoResumida(card);

        return new VagaColetada(
            titulo,
            url,
            descricaoBruta,
            nomeEmpresa,
            localizacao,
            modalidadeTexto,
            null, 
            senioridadeTexto,
            null, 
            null,
            dataPublicacao
        );
        
    }

    private String textoDoIcone(Element card, String classeIcone) {
        Element div = card.selectFirst("div:has(svg." + classeIcone + ")");
        return div != null ? div.text().trim() : null;
    }

    private String extrairDescricaoResumida(Element card) {
        Elements candidatos = card.select("div.text-medium");
        for (Element div : candidatos) {
            String texto = div.text();
            if (texto.contains("Descrição da Vaga")) {
                return texto.replace("Descrição da Vaga", "").trim();
            }
        }
        return null;
    }

    private LocalDate extrairDataPublicacao(Element card) {
        Element divData = card.selectFirst("div.js_date");
        if (divData == null) return LocalDate.now();

        String valorData = divData.attr("data-value");
        if (valorData.isBlank()) return LocalDate.now();

        try {
            return LocalDateTime.parse(valorData, FORMATO_DATA_INFOJOBS).toLocalDate();
        } catch (Exception e) {
            log.warn("Não foi possível parsear data '{}', usando data atual", valorData);
            return LocalDate.now();
        }
    }

    private String textoOuNull(Element card, String seletor) {
        Element elemento = card.selectFirst(seletor);
        return elemento != null ? elemento.text().trim() : null;
    }
}
