package com.daniel_p_d.extractor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarPreferenciaPersonalizadaRequest(
    @NotBlank(message = "Informe um termo")
    @Size(min = 3, max = 60, message = "O termo deve ter entre 3 e 60 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} -]+$", message = "Use apenas letras, números, espaços e hífen")
    String termo
) {}
