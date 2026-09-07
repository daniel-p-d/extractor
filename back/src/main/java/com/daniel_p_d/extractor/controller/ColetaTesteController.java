package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
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
    private final CategoriaRepository categoriaRepository;

    @PostMapping("/{categoriaId}/infojobs")
    public String executarInfoJobs(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(infoJobsScraper, categoria);
        return "InfoJobs - Coleta iniciada, confira os logs";
    }

    @PostMapping("/{categoriaId}/catho")
    public String executarCatho(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(cathoScraper, categoria);
        return "Catho - Coleta iniciada, confira os logs";
    }

    @PostMapping("/{categoriaId}/vagascom")
    public String executarvagascom(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(vagasComScraper, categoria);
        return "Vagas.com - Coleta iniciada, confira os logs";
    }
}
