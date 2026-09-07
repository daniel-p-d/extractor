package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.dto.response.CategoriaResponse;
import com.daniel_p_d.extractor.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listarAtivas() {
        return categoriaRepository.findByAtivaTrue().stream()
            .map(c -> new CategoriaResponse(c.getId(), c.getNome()))
            .toList();
    }
}
