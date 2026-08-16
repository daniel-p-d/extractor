package com.daniel_p_d.extractor.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String login,

    @NotBlank
    String senha
) {
}  
