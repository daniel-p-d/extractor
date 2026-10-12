package com.daniel_p_d.extractor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoriaResponse(
    Long id, 
    @Schema(description = "Nome exibido da categoria", example = "Suporte Técnico") String nome
) {
    
}
