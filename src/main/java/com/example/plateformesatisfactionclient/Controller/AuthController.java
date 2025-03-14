package com.example.plateformesatisfactionclient.Controller;

import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.RoleRepository;
import com.example.plateformesatisfactionclient.Service.Authservice;
import com.example.plateformesatisfactionclient.Service.Emailservice;
import com.example.plateformesatisfactionclient.payload.request.LoginRequest;
import com.example.plateformesatisfactionclient.payload.request.SignupRequest;
import com.example.plateformesatisfactionclient.payload.response.JwtResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // 🔥 Autoriser les requêtes du frontend
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CLIENT_ID = "678352302593-efqco2fe19sb705grc97nni2q8k8q49p.apps.googleusercontent.com";

    private final Authservice authService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private Emailservice emailService;


    public AuthController(Authservice authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        String response = authService.registerUser(signUpRequest);
        if (response.startsWith("Error")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        List<String> roles = roleRepository.findAll()
                .stream()
                .map(role -> role.getName().name()) // Convertit `ERole` en String
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        // Générer un token unique (UUID)
        String token = UUID.randomUUID().toString();

        // Créer un lien de réinitialisation (remplace localhost par ton vrai domaine)
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        // Envoyer l'email
        emailService.sendResetPasswordEmail(email, resetLink);

        return "Un email de réinitialisation a été envoyé à " + email;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            // Appeler le service pour réinitialiser le mot de passe
            String response = authService.resetPassword(email, newPassword);
            return ResponseEntity.ok(response);  // Réponse OK avec le message de succès
        } catch (Exception e) {
            // Si une erreur survient (utilisateur non trouvé)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
    @PostMapping("/google")
    public ResponseEntity<String> authenticateWithGoogle(@RequestBody Map<String, String> request) {
        try {
            String idTokenString = request.get("idToken");

            if (idTokenString == null || idTokenString.isEmpty()) {
                return ResponseEntity.status(400).body("ID token is missing or empty");
            }

            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(400).body("Invalid ID token");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(500).body("Error during token verification: " + e.getMessage());
        }
    }
}
