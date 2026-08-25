package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.scrapper.portais.InfoJobsScraper;
import com.daniel_p_d.extractor.services.ExecutorColetaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coleta")
@RequiredArgsConstructor
public class ColetaTesteController {

    private final ExecutorColetaService executorColetaService;
    private final InfoJobsScraper infoJobsScraper;

    @PostMapping("/infojobs")
    public String executarInfoJobs() {
        executorColetaService.executar(infoJobsScraper);
        return "Coleta iniciada, confira os logs";
    }
}
