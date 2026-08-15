package com.daniel_p_d.extractor.model;

import com.daniel_p_d.extractor.model.Enums.CategoriaHabilidade;

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
@Table(name = "habilidades", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaHabilidade categoria;
}
