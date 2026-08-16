package com.daniel_p_d.extractor.config.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(UserDetails userDetails){
        Date agr = new Date();
        Date expiracao = new Date(agr.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(agr)
                .expiration(expiracao)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver){
        Claims claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

        return resolver.apply(claims);
    }

    public String extrairLogin(String token){
        return extrairClaim(token, Claims::getSubject);
    }

    private boolean tokenExpirado(String token){
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    public Long getExpirationMs(){
        return expirationMs;
    }

    public boolean tokenValido(String token, UserDetails userDetails){
        String login = extrairLogin(token);
        return login.equals(userDetails.getUsername()) && !tokenExpirado(token);
    }
}
