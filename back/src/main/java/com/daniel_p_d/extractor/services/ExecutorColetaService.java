package com.daniel_p_d.extractor.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.HistoricoColeta;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.model.Vaga;
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
    private final NotificacaoService notificacaoService;

    public void executar(ScraperClient scraper, Categoria categoria) {
        Portal portal = portalService.buscaPorNome(scraper.nomePortal());
        HistoricoColeta historico = historicoColetaService.iniciarColeta(portal);

        int novas = 0;
        int comErro = 0;

        List<Vaga> vagasNovas = new ArrayList<>();

        try {
            List<VagaColetada> vagasColetadas = scraper.coletar(categoria);

            for (VagaColetada dados : vagasColetadas) {
                try {
                    var resultado = vagaService.processarVagaColetada(dados, portal, categoria);
                    if (resultado.novaVaga()) {
                        novas++;
                        vagasNovas.add(resultado.vaga());
                    }
                } catch (Exception e) {
                    log.error("{} [{}] - Erro ao processar vaga individual: {}",
                        scraper.nomePortal(), categoria.getNome(), e.getMessage());
                    comErro++;
                }
            }

            historicoColetaService.finalizarComSucesso(historico, vagasColetadas.size(), novas, comErro);

            notificacaoService.notificarSobreVagasNovas(vagasNovas);

            log.info("{} [{}] - Coleta finalizada: {} encontradas, {} novas, {} com erro",
                scraper.nomePortal(), categoria.getNome(), vagasColetadas.size(), novas, comErro);

        } catch (Exception e) {
            historicoColetaService.finalizarComErro(historico, e.getMessage());
            log.error("{} [{}] - Coleta falhou completamente: {}",
                scraper.nomePortal(), categoria.getNome(), e.getMessage(), e);
        }
    }
}
