package com.daniel_p_d.extractor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CriarPreferenciaRequest(
    @Schema(description = "ID de uma categoria do catálogo (GET /categorias)", example = "1")
    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId
) {
    
}
