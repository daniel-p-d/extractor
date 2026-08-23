package com.daniel_p_d.extractor.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VagaColetada(
    String titulo,
    String url,
    String descricaoBruta,
    String nomeEmpresa,
    String localizacao,
    String modalidadeTexto,
    String regimeTexto,
    String senioridadeTexto,
    BigDecimal salarioMin,
    BigDecimal salarioMax,
    LocalDate dataPublicacao
) {
    
}
