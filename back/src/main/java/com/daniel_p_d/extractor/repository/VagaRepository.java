package com.daniel_p_d.extractor.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;


public interface VagaRepository extends JpaRepository<Vaga, Long>{

    boolean existsByUrl(String url);

    Optional<Vaga> findByUrl(String url);
    Page<Vaga> findByStatus(StatusVaga status, Pageable pageable);

    Page<Vaga> findByPortalId(Long portalID, Pageable pageable);

    List<Vaga> findByDataPublicacaoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT v FROM Vaga v WHERE v.dataPublicacao >= :inicio AND v.dataPublicacao <= :fim AND v.status = :status")
    List<Vaga> buscarPorPeriodoEStatus(
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim,
        @Param("status") StatusVaga status
    );

    long countByPortalIdAndDataPublicacaoBetween(Long portalId, LocalDate inicio, LocalDate fim);

    long countByCategoriaIdAndDataPublicacaoBetween(Long categoriaId, LocalDate inicio, LocalDate fim);
}
