package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.services.AnaliseTendenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/analise")
@RequiredArgsConstructor
@Tag(name = "Administração", description = "Disparos manuais (exigem perfil ADMIN)")
public class AnaliseTesteController {
    
    private final AnaliseTendenciaService analiseTendenciaService;

    @Operation(summary = "Recalcula a análise mensal de tendências")
    @PostMapping("/calcular")
    public String calcular() {
        analiseTendenciaService.calcularAnaliseDoMesAtual();
        return "Cálculo de análise iniciado, confira os logs e o banco";
    }
}
