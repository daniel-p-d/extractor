package com.daniel_p_d.extractor.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historico_coleta")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoColeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portal_id", nullable = false)
    private Portal portal;

    @Column(name = "iniciado_em", nullable = false)
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @Column(name = "vagas_encontradas")
    private int vagasEncontradas;

    @Column(name = "vagas_novas")
    private int vagasNovas;

    @Column(name = "vagas_com_erro")
    private int vagasComErro;

    @Column(nullable = false)
    private boolean sucesso;

    @Lob
    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;
}
