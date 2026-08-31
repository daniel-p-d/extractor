package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.scrapper.portais.CathoScraper;
import com.daniel_p_d.extractor.scrapper.portais.InfoJobsScraper;
import com.daniel_p_d.extractor.scrapper.portais.VagasComScraper;
import com.daniel_p_d.extractor.services.ExecutorColetaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coleta")
@RequiredArgsConstructor
public class ColetaTesteController {

    private final ExecutorColetaService executorColetaService;
    private final InfoJobsScraper infoJobsScraper;
    private final CathoScraper  cathoScraper;
    private final VagasComScraper vagasComScraper;

    @PostMapping("/infojobs")
    public String executarInfoJobs() {
        executorColetaService.executar(infoJobsScraper);
        return "InfoJobs - Coleta iniciada, confira os logs";
    }

    @PostMapping("/catho")
    public String executarCatho() {
        executorColetaService.executar(cathoScraper);
        return "Catho - Coleta iniciada, confira os logs";
    }

    @PostMapping("/vagascom")
    public String executarvagascom() {
        executorColetaService.executar(vagasComScraper);
        return "Vagas.com - Coleta iniciada, confira os logs";
    }
}
