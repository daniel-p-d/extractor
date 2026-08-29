package com.daniel_p_d.extractor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.AlertaUsuario;

public interface AlertaUsuarioRepository extends JpaRepository<AlertaUsuario, Long> {
    List<AlertaUsuario> findByUsuarioId(Long usuarioId);
    List<AlertaUsuario> findByAtivoTrue();
    List<AlertaUsuario> findByAtivoTrueAndHabilidadeIdIn(List<Long> habilidadeIds);
    boolean existsByUsuarioIdAndHabilidadeId(Long usuarioId, Long habilidadeId);
    
}
