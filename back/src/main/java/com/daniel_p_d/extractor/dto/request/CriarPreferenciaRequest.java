package com.daniel_p_d.extractor.dto.request;

import jakarta.validation.constraints.NotNull;

public record CriarPreferenciaRequest(
    @NotNull(message = "Categoria é obrigatória")
    Long categoriaId
) {
    
}
