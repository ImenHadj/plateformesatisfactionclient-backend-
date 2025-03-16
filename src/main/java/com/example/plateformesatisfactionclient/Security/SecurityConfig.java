package com.example.plateformesatisfactionclient.Security;

import com.example.plateformesatisfactionclient.Security.jwt.AuthEntryPointJwt;
import com.example.plateformesatisfactionclient.Security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter; // Déclaration du filtre JWT
    private final AuthEntryPointJwt authEntryPointJwt; // Déclaration de l'EntryPoint

    // Injecter les dépendances JwtFilter et JwtAuthenticationEntryPoint
    public SecurityConfig(@Lazy JwtFilter jwtFilter, @Lazy AuthEntryPointJwt authEntryPointJwt) {
        this.jwtFilter = jwtFilter;
        this.authEntryPointJwt = authEntryPointJwt;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors() // ✅ Active la configuration CORS définie dans corsConfigurationSource()
                .and()
                .csrf().disable() // Désactiver CSRF pour les appels API
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/signup", "/api/auth/signin", "/api/auth/roles",
                                "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/google")
                        .permitAll()  // Autoriser l'accès public
                        .requestMatchers("/admin/enquetes/create").authenticated() // Rendre la création d'enquête protégée

                        .anyRequest().authenticated()  // Toute autre requête nécessite une authentification
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPointJwt))  // Gestion des erreurs d'authentification
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Ajout du filtre JWT avant le filtre d'authentification classique

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(authProvider));
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // ✅ Important pour gérer les cookies et tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}