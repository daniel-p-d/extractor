package com.daniel_p_d.extractor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daniel_p_d.extractor.model.PreferenciaUsuario;

public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuario, Long>{
    List<PreferenciaUsuario> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);
    List<PreferenciaUsuario> findByAtivaTrueAndCategoriaIdIn(List<Long> categoriaIds);

    @Query("SELECT p.categoria.id FROM PreferenciaUsuario p WHERE p.usuario.id = :usuarioId AND p.ativa = true")
    List<Long> findCategoriaIdsAtivas(@Param("usuarioId") Long usuarioId);

    Optional<PreferenciaUsuario> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);

    @Query("SELECT COUNT(p) FROM PreferenciaUsuario p WHERE p.usuario.id = :usuarioId AND p.ativa = true")
    long contarAtivas(@Param("usuarioId") Long usuarioId);
}
