package com.daniel_p_d.extractor.dto.response;

import java.util.List;

public record PaginaResponse<T>(
    List<T> conteudo,
    int pagina,
    int totalPaginas,
    long totalElementos
) {
    
}
