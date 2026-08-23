package com.daniel_p_d.extractor.services;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.exception.PortalException;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.repository.PortalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortalService {
    
    private final PortalRepository portalRepository;

    public Portal buscaPorNome(String nome){
        return portalRepository.findByNomeIgnoreCase(nome).orElseThrow(() -> new PortalException("Portal não cadastrado: " + nome));
    }
}
