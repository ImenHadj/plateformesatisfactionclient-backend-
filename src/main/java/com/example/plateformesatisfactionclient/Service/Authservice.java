package com.example.plateformesatisfactionclient.Service;

import com.example.plateformesatisfactionclient.Entity.ERole;
import com.example.plateformesatisfactionclient.Entity.Role;
import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.RoleRepository;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import com.example.plateformesatisfactionclient.Security.jwt.JwtUtil;
import com.example.plateformesatisfactionclient.payload.request.LoginRequest;
import com.example.plateformesatisfactionclient.payload.request.SignupRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class Authservice {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔐 Authenticate User & Generate JWT Token
    /*public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(loginRequest.getUsername());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // ✅ Convert Set<Role> to List<String>
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roleNames);
    }*/
    public void authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {
        // Authentification de l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du JWT
        String jwt = jwtUtil.generateToken(loginRequest.getUsername());

        // Création du cookie JWT
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);  // Empêche l'accès au cookie via JavaScript
        cookie.setSecure(true);    // Doit être activé en production (HTTPS)
        cookie.setPath("/");       // Accessible sur toute l'application
        cookie.setMaxAge(24 * 60 * 60);  // Expire après 24h

        // Ajout du cookie à la réponse
        response.addCookie(cookie);
    }

    private boolean isSecureEnvironment(HttpServletRequest request) {
        return "https".equalsIgnoreCase(request.getScheme());
    }


    // 📝 Register New User
    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return "Error: Username is already taken!";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return "Error: Email is already in use!";
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Assigning a default role if none is provided
            Role userRole = roleRepository.findByName(ERole.ROLE_Client)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role foundRole = roleRepository.findByName(ERole.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(foundRole);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully!";
    }

    public String resetPassword(String email, String newPassword) {
        // Vérifier si l'utilisateur existe avec cet email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé avec cet email.");
        }

        // Mettre à jour le mot de passe de l'utilisateur
        user.setPassword(passwordEncoder.encode(newPassword));  // N'oubliez pas de crypter le mot de passe
        userRepository.save(user);  // Enregistrer les changements dans la base de données

        return "Mot de passe réinitialisé avec succès.";
    }

}
