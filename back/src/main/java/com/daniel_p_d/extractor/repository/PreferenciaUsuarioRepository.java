package com.daniel_p_d.extractor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.PreferenciaUsuario;

public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuario, Long>{
    List<PreferenciaUsuario> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);
}
