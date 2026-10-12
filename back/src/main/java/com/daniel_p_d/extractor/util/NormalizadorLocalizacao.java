package com.daniel_p_d.extractor.util;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NormalizadorLocalizacao {
    
    private static final Pattern UF_FINAL = Pattern.compile("(?:/|-|,)\\s*([A-Z]{2})\\s*$");

    private static final Map<String, String> ESTADOS = Map.ofEntries(
        Map.entry("acre", "AC"), Map.entry("alagoas", "AL"), Map.entry("amapa", "AP"),
        Map.entry("amazonas", "AM"), Map.entry("bahia", "BA"), Map.entry("ceara", "CE"),
        Map.entry("distrito federal", "DF"), Map.entry("espirito santo", "ES"), Map.entry("goias", "GO"),
        Map.entry("maranhao", "MA"), Map.entry("mato grosso", "MT"), Map.entry("mato grosso do sul", "MS"),
        Map.entry("minas gerais", "MG"), Map.entry("para", "PA"), Map.entry("paraiba", "PB"),
        Map.entry("parana", "PR"), Map.entry("pernambuco", "PE"), Map.entry("piaui", "PI"),
        Map.entry("rio de janeiro", "RJ"), Map.entry("rio grande do norte", "RN"),
        Map.entry("rio grande do sul", "RS"), Map.entry("rondonia", "RO"), Map.entry("roraima", "RR"),
        Map.entry("santa catarina", "SC"), Map.entry("sao paulo", "SP"), Map.entry("sergipe", "SE"),
        Map.entry("tocantins", "TO")
    );

    private NormalizadorLocalizacao() {}

    public static String paraUf(String bruto) {
        if (bruto == null || bruto.isBlank()) return "Não informada";
        String t = bruto.trim();
        String sem = semAcento(t).toLowerCase();

        if (sem.contains("home office") || sem.contains("remoto") || sem.contains("todo brasil")) {
            return "Remoto / Todo Brasil";
        }
        if (sem.contains("nao informada")) return "Não informada";

        Matcher m = UF_FINAL.matcher(t);
        if (m.find() && ESTADOS.containsValue(m.group(1))) return m.group(1);

        // Catho
        String uf = ESTADOS.get(sem.split(",")[0].trim());
        return uf != null ? uf : "Não identificada";
    }

    private static String semAcento(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
