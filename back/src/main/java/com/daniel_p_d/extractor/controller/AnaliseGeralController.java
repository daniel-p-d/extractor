package com.daniel_p_d.extractor.controller;

import com.daniel_p_d.extractor.dto.response.ResumoAnaliseResponse;
import com.daniel_p_d.extractor.services.AnaliseGeralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analises")
@RequiredArgsConstructor
public class AnaliseGeralController {

    private final AnaliseGeralService analiseGeralService;

    @GetMapping("/resumo")
    public ResumoAnaliseResponse resumo(
        @RequestParam Long categoriaId,
        @RequestParam(defaultValue = "30") int dias
    ) {
        return analiseGeralService.resumo(categoriaId, dias);
    }
}
