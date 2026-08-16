package com.daniel_p_d.extractor.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @Nonnull HttpServletRequest request, 
        @Nonnull HttpServletResponse response, 
        @Nonnull FilterChain filterChain)
        throws ServletException, IOException {
       
       String authHeader = request.getHeader("Authorization");
       
       if(authHeader == null || !authHeader.startsWith("Bearer")){
        filterChain.doFilter(request, response);
        return;
       }

       String token = authHeader.substring(7);
       String login = jwtService.extrairLogin(token);

       if(login != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

        if(jwtService.tokenValido(token, userDetails)){
            var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
       }

       filterChain.doFilter(request, response);
    }
}
