package com.daniel_p_d.extractor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
    @Schema(description = "Token JWT para o cabeçalho Authorization") 
    String token,

    @Schema(description = "Tipo do token", example = "Bearer") 
    String tipo,

    @Schema(description = "Validade do token, em segundos", example = "86400")
    Long expiracao,
    UsuarioResponse usuario
) {
    
}
