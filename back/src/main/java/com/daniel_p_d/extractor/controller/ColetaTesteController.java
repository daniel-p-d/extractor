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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coleta")
@RequiredArgsConstructor
@Tag(name = "Administração", description = "Disparos manuais (exigem perfil ADMIN)")
public class ColetaTesteController {

    private final ExecutorColetaService executorColetaService;
    private final InfoJobsScraper infoJobsScraper;
    private final CathoScraper  cathoScraper;
    private final VagasComScraper vagasComScraper;
    private final CategoriaRepository categoriaRepository;

    @Operation(summary = "Dispara a coleta manual do InfoJobs")
    @PostMapping("/{categoriaId}/infojobs")
    public String executarInfoJobs(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(infoJobsScraper, categoria);
        return "InfoJobs - Coleta iniciada";
    }

    @Operation(summary = "Dispara a coleta manual do Catho")
    @PostMapping("/{categoriaId}/catho")
    public String executarCatho(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(cathoScraper, categoria);
        return "Catho - Coleta iniciada";
    }

    @Operation(summary = "Dispara a coleta manual do Vagas.com")
    @PostMapping("/{categoriaId}/vagascom")
    public String executarvagascom(@PathVariable Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElseThrow();
        executorColetaService.executar(vagasComScraper, categoria);
        return "Vagas.com - Coleta iniciada";
    }
}
