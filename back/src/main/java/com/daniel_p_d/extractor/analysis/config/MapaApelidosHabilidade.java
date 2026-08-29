package com.daniel_p_d.extractor.analysis.config;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapaApelidosHabilidade {
    
    private static final Map<String, List<String>> APELIDOS = Map.ofEntries(
        Map.entry("JavaScript", List.of("Java Script", "JS")),
        Map.entry("Spring Boot", List.of("Springboot")),
        Map.entry("PostgreSQL", List.of("Postgres")),
        Map.entry("TypeScript", List.of("TS")),
        Map.entry("Node.js", List.of("NodeJS", "Node JS")),
        Map.entry("Kubernetes", List.of("K8s"))
    );

    public List<String> apelidosPara(String nomeOficial) {
        return APELIDOS.getOrDefault(nomeOficial, List.of());
    }
}
