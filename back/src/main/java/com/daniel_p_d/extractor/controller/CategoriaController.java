package com.daniel_p_d.extractor.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.dto.response.CategoriaResponse;
import com.daniel_p_d.extractor.services.CategoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listarAtivas();
    }
}
