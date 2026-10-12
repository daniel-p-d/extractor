package com.daniel_p_d.extractor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PreferenciaUsuarioResponse(
    @Schema(description = "ID da preferência (usado no DELETE)") Long id, 
    @Schema(description = "ID da categoria acompanhada") Long categoriaId,
    String nomeCategoria, 
    @Schema(description = "False quando o usuário removeu") Boolean ativa
) {
    
}
