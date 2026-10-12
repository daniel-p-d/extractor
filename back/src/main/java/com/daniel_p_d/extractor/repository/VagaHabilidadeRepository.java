package com.daniel_p_d.extractor.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daniel_p_d.extractor.model.VagaHabilidade;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;

public interface VagaHabilidadeRepository extends JpaRepository<VagaHabilidade, Long>{

    List<VagaHabilidade> findByVagaId(Long vagaId);
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
    
    @Query("""
        SELECT vh.habilidade.id AS habilidadeId, COUNT(DISTINCT vh.vaga.id) AS quantidade
        FROM VagaHabilidade vh
        WHERE vh.vaga.categoria.id = :categoriaId
        AND vh.vaga.dataPublicacao BETWEEN :inicio AND :fim
        GROUP BY vh.habilidade.id
        """)
    List<ContagemHabilidade> contarMencoesPorCategoriaEPeriodo(
        @Param("categoriaId") Long categoriaId,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim
    );

    interface ContagemHabilidade {
        Long getHabilidadeId();
        Long getQuantidade();
    }

    String FILTRO_VAGA = "vh.vaga.status = :status AND vh.vaga.categoria.id = :categoriaId AND vh.vaga.extraidoEm >= :desde AND vh.vaga.extraidoEm < :ate";

    @Query("SELECT vh.habilidade.nome, COUNT(DISTINCT vh.vaga.id) FROM VagaHabilidade vh WHERE " + FILTRO_VAGA
        + " GROUP BY vh.habilidade.nome ORDER BY COUNT(DISTINCT vh.vaga.id) DESC")
    List<Object[]> contarHabilidades(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                    @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);

    @Query("SELECT vh.vaga.senioridade, vh.habilidade.nome, COUNT(DISTINCT vh.vaga.id) FROM VagaHabilidade vh WHERE "
        + FILTRO_VAGA + " GROUP BY vh.vaga.senioridade, vh.habilidade.nome")
    List<Object[]> contarHabilidadesPorSenioridade(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                                @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);
}
