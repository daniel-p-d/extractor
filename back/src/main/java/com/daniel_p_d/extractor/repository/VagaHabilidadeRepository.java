package com.daniel_p_d.extractor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.daniel_p_d.extractor.model.VagaHabilidade;

public interface VagaHabilidadeRepository extends JpaRepository<VagaHabilidade, Long>{

    List<VagaHabilidade> findByVagaIDd(Long vagaId);
    List<VagaHabilidade> findByHabilidadeId(Long habilidadeId); 

    @Query("""
        SELECT COUNT(vh) FROM VagaHabilidade vh
        WHERE vh.habilidade.id = :habilidadeId
        AND vh.vaga.dataPublicacao BETWEEN :inicio AND :fim
        """)
    long contarMencoesPorPeriodo(
        @org.springframework.data.repository.query.Param("habilidadeId") Long habilidadeId,
        @org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio,
        @org.springframework.data.repository.query.Param("fim") java.time.LocalDate fim
    );
    
}
