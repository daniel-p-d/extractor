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

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/analises")
@RequiredArgsConstructor
public class AnaliseTendenciaController {

    private final AnaliseTendenciaService analiseTendenciaService;

    @GetMapping
    public List<AnaliseTendenciaResponse> consultar(
        @RequestParam Long categoriaId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo
    ) {
        LocalDate periodoConsultado = (periodo != null) ? periodo : LocalDate.now();
        return analiseTendenciaService.consultarPorCategoriaEPeriodo(categoriaId, periodoConsultado);
    }
    
}
