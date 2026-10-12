package com.daniel_p_d.extractor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.config.security.CustomUserDetails;
import com.daniel_p_d.extractor.dto.request.CriarPreferenciaPersonalizadaRequest;
import com.daniel_p_d.extractor.dto.request.CriarPreferenciaRequest;
import com.daniel_p_d.extractor.dto.response.PreferenciaUsuarioResponse;
import com.daniel_p_d.extractor.services.PreferenciaUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/preferencias")
@RequiredArgsConstructor
@Tag(name = "Preferências", description = "Categorias que o usuário acompanha (definem coleta, vagas e e-mails)")
public class PreferenciaUsuarioController {
    private final PreferenciaUsuarioService preferenciaUsuarioService;

    @Operation(summary = "Acompanha uma categoria do catálogo", description = "Limite de 2 categorias ativas por usuário.")
    @PostMapping
    public ResponseEntity<PreferenciaUsuarioResponse> criar(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CriarPreferenciaRequest request) {
        var response = preferenciaUsuarioService.criar(userDetails.getUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista as preferências do usuário", description = "Inclui as inativas (campo 'ativa').")
    @GetMapping
    public ResponseEntity<List<PreferenciaUsuarioResponse>> listarMinhas(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(preferenciaUsuarioService.listarPorUsuario(userDetails.getUsuario().getId()));
    }

    @Operation(summary = "Desativa uma preferência")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        preferenciaUsuarioService.desativar(id, userDetails.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Acompanha um termo livre (ex.: analista de dados)",
        description = "Reaproveita a categoria se o termo já existir. Conta no limite de 2 categorias ativas.")
    @PostMapping("/personalizada")
    public ResponseEntity<PreferenciaUsuarioResponse> criarPersonalizada(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CriarPreferenciaPersonalizadaRequest request
    ) {
        var response = preferenciaUsuarioService.criarPersonalizada(userDetails.getUsuario(), request.termo());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
