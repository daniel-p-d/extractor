package com.daniel_p_d.extractor.controller;

import com.daniel_p_d.extractor.dto.response.PaginaResponse;
import com.daniel_p_d.extractor.dto.response.VagaResponse;
import com.daniel_p_d.extractor.services.VagaConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaConsultaService vagaConsultaService;

    @GetMapping
    public PaginaResponse<VagaResponse> listar(
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "20") int tamanho
    ) {
        return vagaConsultaService.listar(categoriaId, pagina, tamanho);
    }
}
