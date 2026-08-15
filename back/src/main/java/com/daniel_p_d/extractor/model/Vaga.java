package com.daniel_p_d.extractor.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.daniel_p_d.extractor.model.Enums.Modalidade;
import com.daniel_p_d.extractor.model.Enums.NivelSenioridade;
import com.daniel_p_d.extractor.model.Enums.Regime;
import com.daniel_p_d.extractor.model.Enums.StatusVaga;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "vaga",
    uniqueConstraints = @UniqueConstraint(columnNames = "url"),
    indexes = {
        @Index(name = "idx_vaga_status", columnList = "status"),
        @Index(name = "idx_vaga_data_publicacao", columnList = "data_publicacao")
    }
)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "habilidades")
public class Vaga {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(name = "url", nullable = false, unique = true, length = 1000)
    private String url;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_id", nullable = false)
    private Portal Portal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(length = 255)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @Enumerated(EnumType.STRING)
    private Regime regime;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private NivelSenioridade senioridade;

    @Column(name = "salario_min",precision = 12, scale = 2)
    private BigDecimal salarioMin;

    @Column(name = "salario_max",precision = 12, scale = 2)
    private BigDecimal salarioMax;

    @Column(name = "data_publicacao")
    private LocalDate dataPublicacao;

    @CreationTimestamp
    @Column(name = "extraido_em", nullable = false, updatable = false)
    private LocalDate extraidoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StatusVaga status = StatusVaga.ATIVA;

    @OneToMany(mappedBy = "vaga", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VagaHabilidade> habilidades = new ArrayList<>();
}
