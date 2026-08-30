package com.daniel_p_d.extractor.scrapper.portais;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CathoOfferDetailResponse {

    @JsonProperty("o")
    private Offer o;

    @JsonProperty("c")
    private Company c;

    public Offer getO() { return o; }
    public Company getC() { return c; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Offer {
        @JsonProperty("t") private String titulo;
        @JsonProperty("ur") private String urlRelativa;
        @JsonProperty("ld") private String descricao;
        @JsonProperty("lns") private List<String> localizacoes;
        @JsonProperty("uwt") private String modalidade;
        @JsonProperty("lsj") private String tipoContrato;
        @JsonProperty("sn") private String salarioTexto;
        @JsonProperty("smin") private Double salarioMin;
        @JsonProperty("smax") private Double salarioMax;
        @JsonProperty("pt") private String dataPublicacao;

        // getters
        public String getTitulo() { return titulo; }
        public String getUrlRelativa() { return urlRelativa; }
        public String getDescricao() { return descricao; }
        public List<String> getLocalizacoes() { return localizacoes; }
        public String getModalidade() { return modalidade; }
        public String getTipoContrato() { return tipoContrato; }
        public String getSalarioTexto() { return salarioTexto; }
        public Double getSalarioMin() { return salarioMin; }
        public Double getSalarioMax() { return salarioMax; }
        public String getDataPublicacao() { return dataPublicacao; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        @JsonProperty("cn") private String nome;

        public String getNome() { return nome; }
    }
    
}
