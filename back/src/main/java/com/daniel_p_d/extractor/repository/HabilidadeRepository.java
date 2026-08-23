package com.daniel_p_d.extractor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.Habilidade;
import com.daniel_p_d.extractor.model.Enums.CategoriaHabilidade;

import java.util.List;


public interface HabilidadeRepository extends JpaRepository<Habilidade, Long>{
    
    Optional<Habilidade> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    List<Habilidade> findByCategoria(CategoriaHabilidade categoria);
    
}
