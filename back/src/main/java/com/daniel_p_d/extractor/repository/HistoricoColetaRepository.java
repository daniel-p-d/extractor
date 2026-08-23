package com.daniel_p_d.extractor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daniel_p_d.extractor.model.HistoricoColeta;

public interface HistoricoColetaRepository extends JpaRepository<HistoricoColeta, Long>{

    List<HistoricoColeta> findByPortalIdOrderByIniciadoEmDesc(Long portalId);

    Optional<HistoricoColeta> findFirstByPortalIdOrderByIniciadoEmDesc(Long portalId);

    List<HistoricoColeta> findBySucessoFalse();
    
}
