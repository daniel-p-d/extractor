package com.daniel_p_d.extractor.dto.response;

public record PreferenciaUsuarioResponse(
    Long id, 
    Long categoriaId,
    String nomeCategoria, 
    Boolean ativa
) {
    
}
