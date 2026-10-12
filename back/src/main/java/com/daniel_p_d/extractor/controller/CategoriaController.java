package com.daniel_p_d.extractor.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.dto.response.CategoriaResponse;
import com.daniel_p_d.extractor.services.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Catálogo de categorias sugeridas")
@SecurityRequirements

public class CategoriaController {
    private final CategoriaService categoriaService;

    @Operation(summary = "Lista as categorias sugeridas", description = "Só o catálogo padrão. Termos personalizados dos usuários não aparecem aqui.")
    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listarAtivas();
    }
}
