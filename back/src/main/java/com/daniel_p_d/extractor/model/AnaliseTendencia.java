package com.daniel_p_d.extractor.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "analise_tendencia",
    indexes = @Index(name = "idx_analise_periodo", columnList = "periodo_referencia")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseTendencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habilidade_id", nullable = false)
    private Habilidade habilidade;

    @Column(name = "periodo_referencia", nullable = false)
    private LocalDateTime periodoReferencia;

    @Column(name = "quantidade_mencoes", nullable = false)
    private int quantidadeMencoes;

    @Column(name = "percentual_vaga", precision = 5, scale = 2)
    private BigDecimal percentualVagas;

    @Column(name = "variacao_percentual", precision = 6, scale = 2)
    private BigDecimal variacaoPercentual;

    @Column(length = 255)
    private String localizacao;
}
