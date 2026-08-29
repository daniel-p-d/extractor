package com.daniel_p_d.extractor.dto.request;

import jakarta.validation.constraints.NotNull;

public record CriarAlertaRequest(
    @NotNull(message = "Habilidade é obrigatória")
    Long habilidadeId
) {
}