package com.daniel_p_d.extractor.dto.response;


public record LoginResponse(
    String token,
    String tipo,
    Long expiracao,
    UsuarioResponse usuario
) {
    
}
