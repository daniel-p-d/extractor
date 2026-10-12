package com.daniel_p_d.extractor.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record AnaliseTendenciaResponse(
    String nomeHabilidade,
    @Schema(description = "Vagas do mês que citam a habilidade") int quantidadeMencoes,
    @Schema(description = "% das vagas da categoria no mês") BigDecimal percentualVagas,
    @Schema(description = "Variação vs. mês anterior; nulo se não há mês anterior") BigDecimal variacaoPercentual 
) {
    
}
