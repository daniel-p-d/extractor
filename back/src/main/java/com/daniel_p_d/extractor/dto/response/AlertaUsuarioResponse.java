package com.daniel_p_d.extractor.dto.response;

import java.time.LocalDateTime;

public record AlertaUsuarioResponse(
    Long id,
    String nomeHabilidade,
    Boolean ativo,
    LocalDateTime criadoEm,
    LocalDateTime ultimaNotificacaoEm

) {
    
}
