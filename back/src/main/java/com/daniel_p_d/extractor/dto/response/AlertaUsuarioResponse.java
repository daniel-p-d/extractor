package com.daniel_p_d.extractor.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record AlertaUsuarioResponse(
    Long id,
    String nomeHabilidade,
    Boolean ativo,
    LocalDateTime criadoEm,
    @Schema(description = "Nulo se ainda não houve notificação")  LocalDateTime ultimaNotificacaoEm

) {
    
}
