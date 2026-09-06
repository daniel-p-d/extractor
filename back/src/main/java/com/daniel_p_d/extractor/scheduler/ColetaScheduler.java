package com.daniel_p_d.extractor.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.scrapper.portais.CathoScraper;
import com.daniel_p_d.extractor.scrapper.portais.InfoJobsScraper;
import com.daniel_p_d.extractor.scrapper.portais.VagasComScraper;
import com.daniel_p_d.extractor.services.ExecutorColetaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ColetaScheduler {
    
    private final ExecutorColetaService executorColetaService;
    private final InfoJobsScraper infoJobsScraper;
    private final CathoScraper cathoScraper;
    private final VagasComScraper vagasComScraper;

    @Scheduled(cron = "${scheduler.infojobs.cron:0 0 6 * * *}")
    public void executarColetaInfoJobs() {
        log.info("Iniciando coleta agendada: InfoJobs");
        executorColetaService.executar(infoJobsScraper);
    }

    @Scheduled(cron = "${scheduler.catho.cron:0 15 6 * * *}")
    public void executarColetaCatho() {
        log.info("Iniciando coleta agendada: Catho");
        executorColetaService.executar(cathoScraper);
    }

    @Scheduled(cron = "${scheduler.vagascom.cron:0 30 6 * * *}")
    public void executarColetaVagasCom() {
        log.info("Iniciando coleta agendada: Vagas.com");
        executorColetaService.executar(vagasComScraper);
    }


}
