package com.daniel_p_d.extractor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Schema(description = "E-mail cadastrado", example = "fulano@email.com") 
    @NotBlank
    String login,

    @Schema(description = "Senha", example = "senha1234")
    @NotBlank
    String senha
) {
}  
