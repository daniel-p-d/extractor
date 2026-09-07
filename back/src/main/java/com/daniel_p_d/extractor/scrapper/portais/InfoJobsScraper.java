package com.daniel_p_d.extractor.scrapper.portais;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.config.JsoupConnector;
import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.scrapper.ScraperClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InfoJobsScraper implements ScraperClient{
    
    private final JsoupConnector jsoupConnector;

    @Value("${scraper.infojobs.delay-ms:600}")
    private long delayEntreRequestsMs;

    private static final String DOMINIO = "https://www.infojobs.com.br";
    private static final String URL_BUSCA = DOMINIO + "/empregos.aspx?palabra=java";

    private static final DateTimeFormatter FORMATO_DATA_INFOJOBS =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public String nomePortal() {
        return "InfoJobs";
    }

    @Override
    public List<VagaColetada> coletar(Categoria categoria) {
        List<VagaColetada> resultado = new ArrayList<>();

        String urlBusca = DOMINIO + "/empregos.aspx?palabra=" +
            URLEncoder.encode(categoria.getTermoBusca(), StandardCharsets.UTF_8);

        Document pagina = jsoupConnector.buscarDocumento(urlBusca);
        Elements cardsDeVaga = pagina.select("div.js_rowCard");

        log.info("InfoJobs [{}]: {} vagas encontradas na página de listagem", categoria.getNome(), cardsDeVaga.size());

        for (Element card : cardsDeVaga) {
            try {
                VagaColetada parcial = extrairVagaDoCard(card);
                if (parcial == null) continue;

                VagaColetada completa = descricaoCompleta(parcial);
                resultado.add(completa);

                aguardarEntreRequests();

            } catch (Exception e) {
                log.warn("Erro ao processar vaga, pulando: {}", e.getMessage());
            }
        }

    return resultado;
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
        String localizacao = extrairLocalizacao(card);
        String modalidadeTexto = textoDoIcone(card, "house-and-building");
        LocalDate dataPublicacao = extrairDataPublicacao(card);

        return new VagaColetada(
            titulo, url, null, nomeEmpresa, localizacao,
            modalidadeTexto, null, titulo,
            null, null, dataPublicacao
        );
    }

    private VagaColetada descricaoCompleta(VagaColetada parcial) {
        try {
            Document paginaVaga = jsoupConnector.buscarDocumento(parcial.url());

            String descricaoCompleta = textoOuNull(paginaVaga, "div.js_vacancyDataPanels");
            descricaoCompleta = limparDescricao(descricaoCompleta);

            if (descricaoCompleta == null) {
                log.warn("Descrição completa não encontrada para {}, mantendo dado da listagem", parcial.url());
                return parcial;
            }

            return new VagaColetada(
                parcial.titulo(), parcial.url(), descricaoCompleta, parcial.nomeEmpresa(),
                parcial.localizacao(), parcial.modalidadeTexto(), parcial.regimeTexto(),
                parcial.senioridadeTexto(), parcial.salarioMin(), parcial.salarioMax(),
                parcial.dataPublicacao()
            );

        } catch (Exception e) {
            log.warn("Falha ao buscar página individual de {}: {}", parcial.url(), e.getMessage());
            return parcial;
        }
    }

    private String limparDescricao(String textoBruto) {
        if (textoBruto == null) return null;

        int index = textoBruto.indexOf("Denunciar vaga");
        if (index != -1) {
            textoBruto = textoBruto.substring(0, index);
        }

        return textoBruto.trim();
    }

    private void aguardarEntreRequests() {
        try {
            Thread.sleep(delayEntreRequestsMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String extrairLocalizacao(Element card) {
        Element div = card.selectFirst("div[class=mb-8]");
        if (div == null) return null;

        Element clone = div.clone();
        clone.select("span.js_divUserVagaDistance").remove();
        return clone.text().trim();
    }

    private String textoDoIcone(Element card, String nomeIcone) {
        Element use = card.selectFirst("use[xlink:href=#" + nomeIcone + "]");
        if (use == null) return null;

        Element divContainer = use.closest("div");
        return divContainer != null ? divContainer.text().trim() : null;
    }

    private LocalDate extrairDataPublicacao(Element card) {
        Element divData = card.selectFirst("div.js_date");
        if (divData == null) return LocalDate.now();

        String valorData = divData.attr("data-value");
        if (valorData.isBlank()) return LocalDate.now();

        try {
            return LocalDateTime.parse(valorData, FORMATO_DATA_INFOJOBS).toLocalDate();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private String textoOuNull(Element base, String seletor) {
        Element elemento = base.selectFirst(seletor);
        return elemento != null ? elemento.text().trim() : null;
    }
}
