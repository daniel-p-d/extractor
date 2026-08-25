package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.HistoricoColeta;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.scrapper.ScraperClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutorColetaService {
    private final PortalService portalService;
    private final VagaService vagaService;
    private final HistoricoColetaService historicoColetaService;

    public void executar(ScraperClient scraper){
        Portal portal = portalService.buscaPorNome(scraper.nomePortal());
        HistoricoColeta historico = historicoColetaService.iniciarColeta(portal);

        int novas = 0;
        int erro = 0;

        try {
            List<VagaColetada> vagaColetadas = scraper.coletar();

            for(VagaColetada dados: vagaColetadas){
                try {
                    var resultado = vagaService.processarVagaColetada(dados, portal);
                    if (resultado.novaVaga()){
                        novas++;
                    }
                } catch (Exception e) {
                   log.error("Erro ao processar vaga individual: {}", e.getMessage());
                    erro++;
                }
            }

            historicoColetaService.finalizarComSucesso(historico, vagaColetadas.size(), novas, erro);
            log.info("Coleta finalizada: {} encontradas, {} novas, {} com erro", vagaColetadas.size(), novas, erro);
        } catch (Exception e) {
            historicoColetaService.finalizarComErro(historico, e.getMessage());
            log.error("Coleta falhou completamente: {}", e.getMessage(), e);
        }
    }
}
