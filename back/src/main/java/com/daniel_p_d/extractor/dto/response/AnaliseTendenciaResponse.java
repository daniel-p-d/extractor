package com.daniel_p_d.extractor.dto.response;

import java.math.BigDecimal;

public record AnaliseTendenciaResponse(
    String nomeHabilidade,
    int quantidadeMencoes,
    BigDecimal percentualVagas,
    BigDecimal variacaoPercentual 
) {
    
}
