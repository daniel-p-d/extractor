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
import com.daniel_p_d.extractor.dto.request.CriarPreferenciaRequest;
import com.daniel_p_d.extractor.dto.response.PreferenciaUsuarioResponse;
import com.daniel_p_d.extractor.services.PreferenciaUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/preferencias")
@RequiredArgsConstructor
public class PreferenciaUsuarioController {
    private final PreferenciaUsuarioService preferenciaUsuarioService;

    @PostMapping
    public ResponseEntity<PreferenciaUsuarioResponse> criar(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CriarPreferenciaRequest request) {
        var response = preferenciaUsuarioService.criar(userDetails.getUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PreferenciaUsuarioResponse>> listarMinhas(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(preferenciaUsuarioService.listarPorUsuario(userDetails.getUsuario().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        preferenciaUsuarioService.desativar(id, userDetails.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }
}
