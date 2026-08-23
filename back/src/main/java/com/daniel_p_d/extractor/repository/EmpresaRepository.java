package com.daniel_p_d.extractor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>{
    
    Optional<Empresa> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
