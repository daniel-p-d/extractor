package com.daniel_p_d.extractor.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.HistoricoColeta;
import com.daniel_p_d.extractor.model.Portal;
import com.daniel_p_d.extractor.repository.HistoricoColetaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoricoColetaService {
   private final HistoricoColetaRepository historicoColetaRepository;

   @Transactional
    public HistoricoColeta iniciarColeta(Portal portal) {
        HistoricoColeta historico = HistoricoColeta.builder()
            .portal(portal)
            .iniciadoEm(LocalDateTime.now())
            .sucesso(false)
            .build();

        return historicoColetaRepository.save(historico);
    }

    @Transactional
    public void finalizarComSucesso(HistoricoColeta historico, int encontradas, int novas, int comErro) {
        historico.setFinalizadoEm(LocalDateTime.now());
        historico.setVagasEncontradas(encontradas);
        historico.setVagasNovas(novas);
        historico.setVagasComErro(comErro);
        historico.setSucesso(true);

        historicoColetaRepository.save(historico);
    }

    @Transactional
    public void finalizarComErro(HistoricoColeta historico, String mensagemErro) {
        historico.setFinalizadoEm(LocalDateTime.now());
        historico.setSucesso(false);
        historico.setMensagemErro(mensagemErro);

        historicoColetaRepository.save(historico);
    }
    
}
