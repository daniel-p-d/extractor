package com.daniel_p_d.extractor.dto.response;

import java.time.LocalDate;
import java.util.List;

public record VagaResponse(
    Long id,
    String titulo,
    String url,
    String empresa,
    String portal,
    String categoria,
    String localizacao,
    String modalidade,
    String senioridade,
    String regime,
    LocalDate dataPublicacao,
    List<String> habilidades
) {
    
}
