package com.daniel_p_d.extractor.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.AnaliseTendencia;

public interface AnaliseTendenciaRepository extends JpaRepository<AnaliseTendencia, Long>{
    List<AnaliseTendencia> findByPeriodoReferencia(LocalDate periodoReferencia);

    List<AnaliseTendencia> findByHabilidadeIdOrderByPeriodoReferenciaDesc(Long habilidadeId);

    boolean existsByHabilidadeIdAndPeriodoReferencia(Long habilidadeId, LocalDate periodoReferencia);
    
}
