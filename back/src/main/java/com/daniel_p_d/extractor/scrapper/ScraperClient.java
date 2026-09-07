package com.daniel_p_d.extractor.scrapper;

import java.util.List;

import com.daniel_p_d.extractor.dto.request.VagaColetada;
import com.daniel_p_d.extractor.model.Categoria;

public interface ScraperClient {
    
    String nomePortal();

    List<VagaColetada> coletar(Categoria categoria);
}
