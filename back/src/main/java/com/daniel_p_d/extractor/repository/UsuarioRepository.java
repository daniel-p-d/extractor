package com.daniel_p_d.extractor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findBylogin(String login);
    boolean existsByLogin(String login);
    
}
