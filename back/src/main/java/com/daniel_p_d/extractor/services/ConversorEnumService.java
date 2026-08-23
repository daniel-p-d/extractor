package com.daniel_p_d.extractor.services;

import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.model.Enums.Modalidade;
import com.daniel_p_d.extractor.model.Enums.NivelSenioridade;
import com.daniel_p_d.extractor.model.Enums.Regime;

@Service
public class ConversorEnumService {
    
    public Modalidade converterModalidade(String texto){
        if (texto == null){
            return Modalidade.NAO_ESPECIFICADO;
        }

        String t = texto.toLowerCase();

        if(t.contains("remoto") || t.contains("home office")){
            return Modalidade.REMOTO;
        }

        if(t.contains("hibrido") || t.contains("hibrido") || t.contains("semi-presencial") || t.contains("semi presencial")){
            return Modalidade.HIBRIDO;
        }

        if(t.contains("presencial")){
            return Modalidade.PRESENCIAL;
        }
        
        return Modalidade.NAO_ESPECIFICADO;
    }

    public Regime converterRegime(String texto){
        if (texto == null){
            return Regime.OUTRO;
        }

        String t = texto.toLowerCase();

        if(t.contains("clt")){
            return Regime.CLT;
        }

        if (t.contains("pessoa juridica") || t.contains("pj") || t.contains("pessoa jurídica")){
            return Regime.PJ;
        }

        if (t.contains("freelancer") || t.contains("freela")){
            return Regime.FREELANCER;
        }

        if (t.contains("estágio") || t.contains("estagio")){
            return Regime.ESTAGIO;
        }

        if (t.contains("temporário") || t.contains("temporario")){
            return Regime.TEMPORARIO;
        }

        return Regime.OUTRO;
    }

    public NivelSenioridade converterSenioridade(String texto){
        if (texto == null){
            return NivelSenioridade.OUTRO;
        }

        String t = texto.toLowerCase();

        if (t.contains("sênior") || t.contains("senior")){
            return NivelSenioridade.SENIOR;
        }
        
        if (t.contains("pleno")){
            return NivelSenioridade.PLENO;
        }

        if (t.contains("junior") || t.contains("júnior")){
            return NivelSenioridade.JUNIOR;
        }

        if (t.contains("especialista")){
            return NivelSenioridade.ESPECIALISTA;
        }

        if (t.contains("estágiario") || t.contains("estagiario") || t.contains("estágio") || t.contains("estagio")){
            return NivelSenioridade.ESTAGIO;
        }
        
        return NivelSenioridade.OUTRO;
    }
}
