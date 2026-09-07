package com.daniel_p_d.extractor.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.model.HistoricoColeta;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.VagaHabilidade;
import com.daniel_p_d.extractor.repository.VagaHabilidadeRepository;
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
    private final VagaHabilidadeRepository vagaHabilidadeRepository;

    public void executar(ScraperClient scraper, Categoria categoria) {
        Portal portal = portalService.buscaPorNome(scraper.nomePortal());
        HistoricoColeta historico = historicoColetaService.iniciarColeta(portal);

        int novas = 0;
        int comErro = 0;
        Map<Vaga, Set<Habilidade>> vagasNovasComHabilidades = new HashMap<>();

        try {
            List<VagaColetada> vagasColetadas = scraper.coletar(categoria);

            for (VagaColetada dados : vagasColetadas) {
                try {
                    var resultado = vagaService.processarVagaColetada(dados, portal, categoria);
                    if (resultado.novaVaga()) {
                        novas++;
                        vagasNovasComHabilidades.put(resultado.vaga(), extrairHabilidadesDaVaga(resultado.vaga()));
                    }
                } catch (Exception e) {
                    log.error("{} [{}] - Erro ao processar vaga individual: {}", scraper.nomePortal(), categoria.getNome(), e.getMessage());
                    comErro++;
                }
            }

            historicoColetaService.finalizarComSucesso(historico, vagasColetadas.size(), novas, comErro);
            notificacaoService.notificarSobreVagasNovas(vagasNovasComHabilidades);

            log.info("{} [{}] - Coleta finalizada: {} encontradas, {} novas, {} com erro",
                scraper.nomePortal(), categoria.getNome(), vagasColetadas.size(), novas, comErro);

        } catch (Exception e) {
            historicoColetaService.finalizarComErro(historico, e.getMessage());
            log.error("{} [{}] - Coleta falhou completamente: {}", scraper.nomePortal(), categoria.getNome(), e.getMessage(), e);
        }
    }

    private Set<Habilidade> extrairHabilidadesDaVaga(Vaga vaga) {
        return vagaHabilidadeRepository.findByVagaId(vaga.getId()).stream()
            .map(VagaHabilidade::getHabilidade)
            .collect(java.util.stream.Collectors.toSet());
    }
}
