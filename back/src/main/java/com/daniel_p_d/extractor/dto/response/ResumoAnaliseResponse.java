package com.daniel_p_d.extractor.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResumoAnaliseResponse(
    String categoria,
    @Schema(description = "Tamanho da janela analisada, em dias") int dias,
    @Schema(description = "Vagas coletadas na janela")long totalVagas,
    Volume volume,
    @Schema(description = "Vagas por portal") List<Item> porPortal,
    @Schema(description = "Vagas por senioridade") List<Item> porSenioridade,
    @Schema(description = "Vagas por UF (top 10)") List<Item> porLocalizacao,
    @Schema(description = "Empresas com mais vagas (top 10, sem 'confidencial')") List<Item> empresas,
    @Schema(description = "Tecnologias mais citadas (top 10)") List<Tecnologia> tecnologias,
    @Schema(description = "Top 5 habilidades de cada nível de senioridade") List<GrupoHabilidades> habilidadesPorSenioridade
) {
    public record Item(String rotulo, long quantidade, 
        @Schema(description = "% sobre o total da dimensão") BigDecimal percentual) {}
    public record Tecnologia(String habilidade, long quantidade, BigDecimal percentual,
         @Schema(description = "Variação em pontos percentuais vs. janela anterior; nulo se não há base") BigDecimal variacaoPontos) {}
    public record Ponto(LocalDate data, long quantidade) {}
    public record Volume(long totalAtual, long totalAnterior,
        @Schema(description = "Variação % do número de vagas vs. janela anterior; nulo se não há base") BigDecimal variacaoPercentual, 
        @Schema(description = "Vagas coletadas por dia na janela")  List<Ponto> serieDiaria) {}
    public record GrupoHabilidades(String senioridade, long totalVagas, List<Item> habilidades) {}
}
