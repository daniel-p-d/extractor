package com.daniel_p_d.extractor.analysis.config;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapaApelidosHabilidade {
    
    private static final Map<String, String> APELIDOS = Map.ofEntries(
        Map.entry("spring boot", "Spring Boot"),
        Map.entry("springboot", "Spring Boot"),
        Map.entry("postgres", "PostgreSQL"),
        Map.entry("postgresql", "PostgreSQL"),
        Map.entry("java", "Java")
    );

    public String resolverNomeOficial(String termoEncontrado) {
        return APELIDOS.getOrDefault(termoEncontrado.toLowerCase(), termoEncontrado);
    }
}
