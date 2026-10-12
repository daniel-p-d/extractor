package com.daniel_p_d.extractor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CriarAlertaRequest(
    @Schema(description = "ID da habilidade monitorada", example = "1")
    @NotNull(message = "Habilidade é obrigatória")
    Long habilidadeId
) {
}