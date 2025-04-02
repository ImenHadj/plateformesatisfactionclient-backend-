package com.example.plateformesatisfactionclient.Controller;

import com.example.plateformesatisfactionclient.Entity.*;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import com.example.plateformesatisfactionclient.Security.jwt.JwtUtil;
import com.example.plateformesatisfactionclient.Service.Authservice;
import com.example.plateformesatisfactionclient.Service.Emailservice;
import com.example.plateformesatisfactionclient.Service.EnqueteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/enquetes")
public class EnqueteController {

    @Autowired
    private EnqueteService enqueteService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnqueteRepository enqueteRepository;
    @Autowired
    private Emailservice emailService;

    @Autowired
    private Authservice userService;
    public EnqueteController(EnqueteService enqueteService, UserRepository userRepository) {
        this.enqueteService = enqueteService;
        this.userRepository = userRepository;
    }





    @PostMapping("/create")
    public ResponseEntity<Enquete> creerEnqueteAvecQuestions(@RequestBody Enquete enquete) {
        // Récupérer l'utilisateur authentifié à partir du JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Vérifier que l'utilisateur a le rôle 'ROLE_ADMIN'
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Utilisateur est un admin, nous récupérons l'utilisateur depuis le token JWT
        String username = authentication.getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé"));

        // Convertir la date de publication et d'expiration
        LocalDateTime publicationDate = enquete.getDatePublication();
        LocalDateTime expirationDate = enquete.getDateExpiration();

        // Créer l'enquête avec les questions
        Enquete savedEnquete = enqueteService.creerEnqueteAvecQuestions(
                enquete.getTitre(),
                enquete.getDescription(),
                publicationDate,
                expirationDate,
                admin,  // Utiliser l'admin récupéré automatiquement
                enquete.getQuestions()
        );

        // Vérification si la date actuelle est supérieure ou égale à la date de publication
        if (LocalDateTime.now().isAfter(publicationDate) || LocalDateTime.now().isEqual(publicationDate)) {
            savedEnquete.setStatut(StatutEnquete.PUBLIEE); // Mettre l'enquête en statut publié
            enqueteRepository.save(savedEnquete); // Sauvegarder les modifications

            // Une fois l'enquête publiée, envoyer le lien aux clients
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            String enqueteLink = "http://localhost:9090/enquete/respond/" + savedEnquete.getId(); // Lien vers l'enquête
            for (User client : clients) {
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink); // Envoi du lien de l'enquête
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnquete);
    }






    // Modifier une enquête
    @PutMapping("/update/{id}")
    public ResponseEntity<Enquete> modifierEnquete(@PathVariable Long id,
                                                   @RequestParam String titre,
                                                   @RequestParam String description,
                                                   @RequestParam String dateExpiration) {
        LocalDateTime expirationDate = LocalDateTime.parse(dateExpiration);
        Enquete enquete = enqueteService.modifierEnquete(id, titre, description, expirationDate);
        return ResponseEntity.ok(enquete);
    }

    // Supprimer une enquête
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> supprimerEnquete(@PathVariable Long id) {
        enqueteService.supprimerEnquete(id);
        return ResponseEntity.ok().build();
    }

    // Ajouter une question à une enquête
    @PostMapping("/{enqueteId}/add-question")
    public ResponseEntity<Question> ajouterQuestion(@PathVariable Long enqueteId,
                                                    @RequestParam String texte,
                                                    @RequestParam List<String> options,
                                                    @RequestParam TypeQuestion type) {
        Question question = enqueteService.ajouterQuestion(enqueteId, texte, options, type);
        return ResponseEntity.ok(question);
    }

    // Récupérer toutes les enquêtes d'un admin
    @GetMapping("/all/{adminId}")
    public ResponseEntity<List<Enquete>> getEnquetesAdmin(@PathVariable Long adminId) {
        // Récupérer l'utilisateur (admin) à partir de son ID
        User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Récupérer les enquêtes de l'admin
        List<Enquete> enquetes = enqueteService.getEnquetesAdmin(admin);
        return ResponseEntity.ok(enquetes);
    }


    // Récupérer les questions d'une enquête
    @GetMapping("/{enqueteId}/questions")
    public ResponseEntity<List<Question>> getQuestionsForEnquete(@PathVariable Long enqueteId) {
        List<Question> questions = enqueteService.getQuestionsForEnquete(enqueteId);
        return ResponseEntity.ok(questions);
    }



}
