package com.daniel_p_d.extractor.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import com.daniel_p_d.extractor.model.Vaga;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;


public interface VagaRepository extends JpaRepository<Vaga, Long>{

    boolean existsByUrl(String url);

    Optional<Vaga> findByUrl(String url);
    //Page<Vaga> findByStatus(StatusVaga status, Pageable pageable);

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

    @EntityGraph(attributePaths = {"portal", "empresa", "categoria"})
    Page<Vaga> findByStatus(StatusVaga status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"portal", "empresa", "categoria"})
    Page<Vaga> findByStatusAndCategoriaId(StatusVaga status, Long categoriaId, Pageable pageable);

    @Query(value = """
        SELECT v FROM Vaga v JOIN FETCH v.portal LEFT JOIN FETCH v.empresa JOIN FETCH v.categoria
        WHERE v.status = :status AND v.categoria.id IN :categoriaIds AND v.extraidoEm >= :desde
        """,
        countQuery = """
        SELECT COUNT(v) FROM Vaga v
        WHERE v.status = :status AND v.categoria.id IN :categoriaIds AND v.extraidoEm >= :desde
        """)
    Page<Vaga> buscarPorCategoriasDesde(@Param("status") StatusVaga status,
            @Param("categoriaIds") List<Long> categoriaIds,
            @Param("desde") LocalDateTime desde, Pageable pageable);

    @Query("SELECT MAX(v.extraidoEm) FROM Vaga v WHERE v.status = :status AND v.categoria.id IN :categoriaIds")
    LocalDateTime ultimaExtracao(@Param("status") StatusVaga status, @Param("categoriaIds") List<Long> categoriaIds);

    String FILTRO = "v.status = :status AND v.categoria.id = :categoriaId AND v.extraidoEm >= :desde AND v.extraidoEm < :ate";

    @Query("SELECT COUNT(v) FROM Vaga v WHERE " + FILTRO)
    long contarNoPeriodo(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                        @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);

    @Query("SELECT v.senioridade, COUNT(v) FROM Vaga v WHERE " + FILTRO + " GROUP BY v.senioridade")
    List<Object[]> contarPorSenioridade(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                        @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);

    @Query("SELECT v.portal.nome, COUNT(v) FROM Vaga v WHERE " + FILTRO + " GROUP BY v.portal.nome ORDER BY COUNT(v) DESC")
    List<Object[]> contarPorPortal(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);

    @Query("SELECT v.localizacao, COUNT(v) FROM Vaga v WHERE " + FILTRO + " GROUP BY v.localizacao")
    List<Object[]> contarPorLocalizacao(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                        @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);

    @Query("""
        SELECT v.empresa.nome, COUNT(v) FROM Vaga v
        WHERE v.status = :status AND v.categoria.id = :categoriaId
        AND v.extraidoEm >= :desde AND v.extraidoEm < :ate
        AND LOWER(v.empresa.nome) NOT IN ('confidencial', 'empresa confidencial', 'cliente')
        GROUP BY v.empresa.nome ORDER BY COUNT(v) DESC
        """)
    List<Object[]> topEmpresas(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                            @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate, Pageable pageable);

    @Query("SELECT v.extraidoEm FROM Vaga v WHERE " + FILTRO)
    List<LocalDateTime> listarExtracoes(@Param("status") StatusVaga status, @Param("categoriaId") Long categoriaId,
                                        @Param("desde") LocalDateTime desde, @Param("ate") LocalDateTime ate);
}
