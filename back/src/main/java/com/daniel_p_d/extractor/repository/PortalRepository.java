package com.daniel_p_d.extractor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.Portal;

public interface PortalRepository extends JpaRepository<Portal, Long>{
    
    Optional<Portal> findbyNomeIgnoreCase(String nome);

    List<Portal> findByAtivoTrue();
}