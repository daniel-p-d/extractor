package com.daniel_p_d.extractor.services;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.daniel_p_d.extractor.config.security.CustomUserDetails;
import com.daniel_p_d.extractor.config.security.JwtService;
import com.daniel_p_d.extractor.dto.request.LoginRequest;
import com.daniel_p_d.extractor.dto.request.RegistroRequest;
import com.daniel_p_d.extractor.dto.response.LoginResponse;
import com.daniel_p_d.extractor.dto.response.UsuarioResponse;
import com.daniel_p_d.extractor.exception.UsuarioException;
import com.daniel_p_d.extractor.model.Usuario;
import com.daniel_p_d.extractor.model.Enums.PerfilUsuario;
import com.daniel_p_d.extractor.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Transactional
    public UsuarioResponse cadastro(RegistroRequest request){
        if (usuarioRepository.existsByLogin(request.login())){
            throw new UsuarioException("Usuário já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
            .nome(request.nome())
            .login(request.login())
            .senha(encoder.encode(request.senha()))
            .perfil(PerfilUsuario.USUARIO)
            .ativo(true)
            .build();

        usuario = usuarioRepository.save(usuario);
        
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getPerfil());
    }

    public LoginResponse login(LoginRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.login(), request.senha())
        );

        Usuario usuario = usuarioRepository.findBylogin(request.login()).orElseThrow();

        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(new CustomUserDetails(usuario));

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs()/1000, new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getPerfil()));
    }
}
