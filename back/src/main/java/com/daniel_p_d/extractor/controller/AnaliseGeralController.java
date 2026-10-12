package com.daniel_p_d.extractor.controller;

import com.daniel_p_d.extractor.dto.response.ResumoAnaliseResponse;
import com.daniel_p_d.extractor.services.AnaliseGeralService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analises")
@RequiredArgsConstructor
@Tag(name = "Análises", description = "Indicadores do mercado por categoria")
public class AnaliseGeralController {

    private final AnaliseGeralService analiseGeralService;

    @Operation(summary = "Resumo da análise de uma categoria",
        description = "Calculado na hora: vagas por portal, senioridade e localização, empresas, "
            + "tecnologias em destaque e habilidades por senioridade. "
            + "As variações comparam a janela atual com a anterior, de mesmo tamanho.")
    @GetMapping("/resumo")
    public ResumoAnaliseResponse resumo(
        @RequestParam Long categoriaId,
        @RequestParam(defaultValue = "30") int dias
    ) {
        return analiseGeralService.resumo(categoriaId, dias);
    }
}
