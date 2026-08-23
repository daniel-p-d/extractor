package com.daniel_p_d.extractor.services;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.Empresa;
import com.daniel_p_d.extractor.repository.EmpresaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    @Transactional
    public Empresa resolverOuCriar(String nomeEmpresa){
        if (nomeEmpresa == null || nomeEmpresa.isBlank()){
            return null;
        }

        String nome = nomeEmpresa.trim();

        return empresaRepository.findByNomeIgnoreCase(nome)
            .orElseGet(() -> empresaRepository.save(
                Empresa.builder()
                        .nome(nome)
                        .build())
                );
    }
}
