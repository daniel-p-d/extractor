package com.daniel_p_d.extractor.controller;

import com.daniel_p_d.extractor.config.security.CustomUserDetails;
import com.daniel_p_d.extractor.dto.response.PaginaResponse;
import com.daniel_p_d.extractor.dto.response.VagaResponse;
import com.daniel_p_d.extractor.services.VagaConsultaService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaConsultaService vagaConsultaService;

    @GetMapping
    public PaginaResponse<VagaResponse> listar(
        @AuthenticationPrincipal CustomUserDetails usuario,
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "20") int tamanho
    ) {
        return vagaConsultaService.listar(usuario.getUsuario().getId(), categoriaId, desde, pagina, tamanho);
    }

    public record UltimaExtracaoResponse(LocalDate data) {}

    @GetMapping("/ultima-extracao")
    public UltimaExtracaoResponse ultimaExtracao(@AuthenticationPrincipal CustomUserDetails usuario) {
        return new UltimaExtracaoResponse(vagaConsultaService.ultimaExtracao(usuario.getUsuario().getId()));
    }
}
