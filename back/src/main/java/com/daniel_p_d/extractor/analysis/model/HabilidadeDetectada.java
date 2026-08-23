package com.daniel_p_d.extractor.analysis.model;

import com.daniel_p_d.extractor.model.Habilidade;

public record HabilidadeDetectada(
    Habilidade habilidade,
    int quantidadeMencoes,
    boolean possivelDiferencial

) {
    
}
