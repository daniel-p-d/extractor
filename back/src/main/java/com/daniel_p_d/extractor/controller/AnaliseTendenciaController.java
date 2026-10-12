package com.daniel_p_d.extractor.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.dto.response.AnaliseTendenciaResponse;
import com.daniel_p_d.extractor.services.AnaliseTendenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/analises")
@RequiredArgsConstructor
@Tag(name = "Análises", description = "Indicadores do mercado por categoria")
public class AnaliseTendenciaController {

    private final AnaliseTendenciaService analiseTendenciaService;

    @Operation(summary = "Habilidades mais citadas em um mês.",
        description = "Lê o cálculo mensal feito pelo agendador. Se 'periodo' for omitido, usa o mês atual.")
    @GetMapping
    public List<AnaliseTendenciaResponse> consultar(
        @RequestParam Long categoriaId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo
    ) {
        LocalDate periodoConsultado = (periodo != null) ? periodo : LocalDate.now();
        return analiseTendenciaService.consultarPorCategoriaEPeriodo(categoriaId, periodoConsultado);
    }
    
}
