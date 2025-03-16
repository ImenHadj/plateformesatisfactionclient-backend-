package com.example.plateformesatisfactionclient.Security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Exclure les endpoints d'authentification (signup, signin, etc.)
        if (path.startsWith("/api/auth/signup") || path.startsWith("/api/auth/signin")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Récupérer le token JWT depuis le cookie
        Cookie jwtCookie = WebUtils.getCookie(request, "jwt"); // Changer "JWT_TOKEN" en "jwt"
        String token = (jwtCookie != null) ? jwtCookie.getValue() : null;

        // Si le token existe, essayer de l'extraire et de valider
        if (token != null) {
            logger.debug("Token extrait : {}", token);
            String username = jwtUtil.extractUsername(token);
            logger.debug("Nom d'utilisateur extrait : {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Valider le token
                if (jwtUtil.validateToken(token)) {
                    logger.debug("Token valide, authentification de l'utilisateur.");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Ajouter l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Token invalide ou expiré.");
                }
            }
        } else {
            logger.debug("Aucun cookie JWT trouvé.");
        }

        filterChain.doFilter(request, response); // Continuer la chaîne de filtres
    }
}