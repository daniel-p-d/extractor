package com.daniel_p_d.extractor.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ResumoAnaliseResponse(
    String categoria,
    int dias,
    long totalVagas,
    Volume volume,
    List<Item> porPortal,
    List<Item> porSenioridade,
    List<Item> porLocalizacao,
    List<Item> empresas,
    List<Tecnologia> tecnologias,
    List<GrupoHabilidades> habilidadesPorSenioridade
) {
    public record Item(String rotulo, long quantidade, BigDecimal percentual) {}
    public record Tecnologia(String habilidade, long quantidade, BigDecimal percentual, BigDecimal variacaoPontos) {}
    public record Ponto(LocalDate data, long quantidade) {}
    public record Volume(long totalAtual, long totalAnterior, BigDecimal variacaoPercentual, List<Ponto> serieDiaria) {}
    public record GrupoHabilidades(String senioridade, long totalVagas, List<Item> habilidades) {}
}
