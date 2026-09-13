package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.services.AnaliseTendenciaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/analise")
@RequiredArgsConstructor
public class AnaliseTesteController {
    
    private final AnaliseTendenciaService analiseTendenciaService;

    @PostMapping("/calcular")
    public String calcular() {
        analiseTendenciaService.calcularAnaliseDoMesAtual();
        return "Cálculo de análise iniciado, confira os logs e o banco";
    }
}
