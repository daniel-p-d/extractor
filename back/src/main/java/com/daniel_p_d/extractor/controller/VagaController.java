package com.daniel_p_d.extractor.controller;

import com.daniel_p_d.extractor.config.security.CustomUserDetails;
import com.daniel_p_d.extractor.dto.response.PaginaResponse;
import com.daniel_p_d.extractor.dto.response.VagaResponse;
import com.daniel_p_d.extractor.services.VagaConsultaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vagas")
@RequiredArgsConstructor
@Tag(name = "Vagas", description = "Consulta das vagas coletadas dos portais") 
public class VagaController {

    private final VagaConsultaService vagaConsultaService;

    @Operation(
        summary = "Lista as vagas das categorias que o usuário acompanha",
        description = "Retorna apenas vagas das categorias ativas do usuário autenticado, paginadas e "
            + "ordenadas pela data de publicação. O parâmetro 'desde' filtra pela data de extração "
            + "(quando o sistema coletou a vaga), e não pela data de publicação no portal.")
    @GetMapping
    public PaginaResponse<VagaResponse> listar(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails usuario,
        
        @Parameter(description = "Restringe a uma das categorias do usuário")
        @RequestParam(required = false) Long categoriaId,

        @Parameter(description = "Data mínima de extração (AAAA-MM-DD)")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "20") int tamanho
    ) {
        return vagaConsultaService.listar(usuario.getUsuario().getId(), categoriaId, desde, pagina, tamanho);
    }

    @Schema(description = "Data da coleta mais recente nas categorias do usuário")
    public record UltimaExtracaoResponse(LocalDate data) {}

    @Operation(summary = "Data da última extração das categorias do usuário")
    @GetMapping("/ultima-extracao")
    public UltimaExtracaoResponse ultimaExtracao(@AuthenticationPrincipal CustomUserDetails usuario) {
        return new UltimaExtracaoResponse(vagaConsultaService.ultimaExtracao(usuario.getUsuario().getId()));
    }
}
