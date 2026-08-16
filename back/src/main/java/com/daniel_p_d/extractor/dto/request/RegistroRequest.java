package com.daniel_p_d.extractor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Login é obrigatório")
    @Email(message = "Login deve ser um e-mail válido")
    String login,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha

) {

}
