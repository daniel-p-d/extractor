package com.daniel_p_d.extractor.scrapper;

import java.util.List;

import com.daniel_p_d.extractor.dto.request.VagaColetada;

public interface ScrapperClient {
    
    String nomePortal();

    List<VagaColetada> coletar();
}
