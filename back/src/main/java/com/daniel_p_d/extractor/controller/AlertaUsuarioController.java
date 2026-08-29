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
import com.daniel_p_d.extractor.dto.request.CriarAlertaRequest;
import com.daniel_p_d.extractor.dto.response.AlertaUsuarioResponse;
import com.daniel_p_d.extractor.services.AlertaUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaUsuarioController {
    private final AlertaUsuarioService alertaUsuarioService;

    @PostMapping
    public ResponseEntity<AlertaUsuarioResponse> criar(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody CriarAlertaRequest request
    ) {
        var response = alertaUsuarioService.criar(userDetails.getUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AlertaUsuarioResponse>> listarMeusAlertas(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(alertaUsuarioService.listarPorUsuario(userDetails.getUsuario().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long id
    ) {
        alertaUsuarioService.desativar(id, userDetails.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }
}
