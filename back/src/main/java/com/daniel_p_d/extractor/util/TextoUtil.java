package com.daniel_p_d.extractor.util;

import java.text.Normalizer;

public class TextoUtil {
    private TextoUtil() {}

    // limpar texto
    public static String nomeLimpo(String texto) {
        String t = texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
        return t.isEmpty() ? t : Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }

    // converte para slug da url
    public static String paraSlug(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    }
}
