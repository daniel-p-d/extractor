package com.daniel_p_d.extractor.dto.response;

import com.daniel_p_d.extractor.model.Enums.PerfilUsuario;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioResponse(
    Long id,
    String nome,
    @Schema(description = "E-mail do usuário") String login,
    @Schema(description = "Perfil de acesso") PerfilUsuario perfi

) {
}
