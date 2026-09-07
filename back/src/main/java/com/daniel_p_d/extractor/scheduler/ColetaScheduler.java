package com.daniel_p_d.extractor.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.scrapper.portais.CathoScraper;
import com.daniel_p_d.extractor.scrapper.portais.InfoJobsScraper;
import com.daniel_p_d.extractor.scrapper.portais.VagasComScraper;
import com.daniel_p_d.extractor.services.ColetaOrquestradorService;
import com.daniel_p_d.extractor.services.ExecutorColetaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ColetaScheduler {
    
    private final ColetaOrquestradorService coletaOrquestradorService;

    @Scheduled(cron = "${scheduler.coleta.cron:0 0 6 * * *}")
    public void executarColetaAgendada() {
        log.info("Disparando ciclo de coleta agendado");
        coletaOrquestradorService.executarColetaCompleta();
    }

}
