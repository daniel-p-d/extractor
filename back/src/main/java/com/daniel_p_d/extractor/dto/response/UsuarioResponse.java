package com.daniel_p_d.extractor.dto.response;

import com.daniel_p_d.extractor.model.Enums.PerfilUsuario;

public record UsuarioResponse(
    Long id,
    String nome,
    String login,
    PerfilUsuario perfi

) {
}
