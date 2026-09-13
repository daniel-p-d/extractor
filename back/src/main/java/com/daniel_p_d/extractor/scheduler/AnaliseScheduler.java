package com.daniel_p_d.extractor.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daniel_p_d.extractor.services.AnaliseTendenciaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class AnaliseScheduler {

    private final AnaliseTendenciaService analiseTendenciaService;

    
    @Scheduled(cron = "${scheduler.analise.cron:0 0 7 * * *}")
    public void executarAnaliseAgendada() {
        log.info("Iniciando cálculo agendado de análise de tendências");
        analiseTendenciaService.calcularAnaliseDoMesAtual();
        log.info("Cálculo de análise de tendências finalizado");
    }
}
