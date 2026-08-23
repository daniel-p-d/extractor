package com.daniel_p_d.extractor.config;

import org.springframework.stereotype.Component;

//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Component
//@RequiredArgsConstructor
@Slf4j
public class JsoupConnector {
    private final String userAgent;
    private final int timeoutMs;

    public JsoupConnector(String userAgent, int timeoutMs){
        this.userAgent = userAgent;
        this.timeoutMs = timeoutMs;
    }

    public Document buscarDocumento(String url) {
        return buscarDocumentoComRetry(url, 3);
    }

    private Document buscarDocumentoComRetry(String url, int tentativasRestantes) {
        try {
            return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .followRedirects(true)
                    .get();
        } catch (Exception e) {
            if (tentativasRestantes <= 1){
                log.error("Falha ao buscar {} após todas as tentativas: {}", url, e.getMessage());
                throw new RuntimeException("Falha ao acessar URL: " + url, e);
            }

            log.warn("Falha ao buscar {} (restam {} tentativas): {}", url, tentativasRestantes - 1, e.getMessage());
            aguardarAntesDeTentarNovamente();
            return buscarDocumentoComRetry(url, tentativasRestantes - 1);
        }
    }

    private void aguardarAntesDeTentarNovamente() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
