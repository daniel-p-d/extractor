package com.daniel_p_d.extractor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @Schema(description = "Nome do usuário", example = "Fulano Silva")
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @Schema(description = "E-mail, usado como login", example = "fulano@email.com")
    @NotBlank(message = "Login é obrigatório")
    @Email(message = "Login deve ser um e-mail válido")
    String login,

    @Schema(description = "Mínimo de 8 caracteres", example = "senha1234")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha

) {

}
