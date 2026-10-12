package com.daniel_p_d.extractor.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record VagaResponse(
    Long id,
    String titulo,
    @Schema(description = "Link da vaga no portal de origem") String url,
    @Schema(description = "Pode ser nulo ou 'Confidencial'") String empresa,
    @Schema(description = "Portal de origem", example = "Catho") String portal,
    String categoria,
    String localizacao,
    @Schema(description = "REMOTO, HIBRIDO, PRESENCIAL ou NAO_ESPECIFICADO") String modalidade,
    @Schema(description = "Inferida do título; OUTRO quando não identificada") String senioridade,
    String regime,
    @Schema(description = "Data de publicação no portal")  LocalDate dataPublicacao,
    @Schema(description = "Quando o sistema coletou a vaga") LocalDateTime extraidoEm,
    @Schema(description = "Tecnologias identificadas na descrição") List<String> habilidades
) {
    
}
