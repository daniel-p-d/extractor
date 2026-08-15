package com.daniel_p_d.extractor.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "portais")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Portal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "url_base", nullable = false, length = 500)
    private String urlBase;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @Column(name = "seletor_config", columnDefinition = "TEXT")
    private String seletorConfig;
    
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}
