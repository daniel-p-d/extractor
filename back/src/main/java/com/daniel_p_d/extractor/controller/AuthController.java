package com.daniel_p_d.extractor.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel_p_d.extractor.dto.request.LoginRequest;
import com.daniel_p_d.extractor.dto.request.RegistroRequest;
import com.daniel_p_d.extractor.dto.response.LoginResponse;
import com.daniel_p_d.extractor.dto.response.UsuarioResponse;
import com.daniel_p_d.extractor.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro e login de usuários")
@SecurityRequirements
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Cadastra um novo usuário", description = "Todo cadastro entra com o perfil USUARIO.")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> cadastro(@Valid @RequestBody RegistroRequest request) {
        UsuarioResponse response = service.cadastro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Faz login e devolve o token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
    
    
}
