package com.daniel_p_d.extractor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.daniel_p_d.extractor.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByAtivaTrue();

    Optional<Categoria> findByNomeIgnoreCase(String nome);
    @Query("""
        SELECT DISTINCT c FROM Categoria c
        JOIN PreferenciaUsuario p ON p.categoria = c
        WHERE c.ativa = true AND p.ativa = true
        """)
    List<Categoria> findCategoriasComPreferenciaAtiva();
}
