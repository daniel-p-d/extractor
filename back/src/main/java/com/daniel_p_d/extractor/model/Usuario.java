package com.daniel_p_d.extractor.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.daniel_p_d.extractor.model.Enums.PerfilUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "login"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    @Builder.Default
    private PerfilUsuario perfil = PerfilUsuario.USUARIO;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false, nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;
}
