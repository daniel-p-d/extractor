package com.daniel_p_d.extractor.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.Categoria;
import com.daniel_p_d.extractor.repository.CategoriaRepository;
import com.daniel_p_d.extractor.scrapper.ScraperClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColetaOrquestradorService {
    private final CategoriaRepository categoriaRepository;
    private final ExecutorColetaService executorColetaService;
    private final List<ScraperClient> scrapers;

    public void executarColetaCompleta() {
        List<Categoria> categoriasAtivas = categoriaRepository.findCategoriasComPreferenciaAtiva();

        if (categoriasAtivas.isEmpty()) {
            log.warn("Nenhuma categoria com preferência ativa encontrada, pulando ciclo de coleta");
            return;
        }

        log.info("Iniciando ciclo de coleta para {} categoria(s): {}",
            categoriasAtivas.size(),
            categoriasAtivas.stream().map(Categoria::getNome).toList());

        for (ScraperClient scraper : scrapers) {
            for (Categoria categoria : categoriasAtivas) {
                executorColetaService.executar(scraper, categoria);
            }
        }
    }
}
