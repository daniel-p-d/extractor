package com.daniel_p_d.extractor.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaginaResponse<T>(
    @Schema(description = "Itens da página") List<T> conteudo,
    @Schema(description = "Página atual (começa em 0)") int pagina,
    int totalPaginas,
    long totalElementos
) {
    
}
