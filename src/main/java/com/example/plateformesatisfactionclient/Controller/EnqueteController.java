package com.example.plateformesatisfactionclient.Controller;

import com.example.plateformesatisfactionclient.Entity.Enquete;
import com.example.plateformesatisfactionclient.Entity.Question;
import com.example.plateformesatisfactionclient.Entity.TypeQuestion;
import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import com.example.plateformesatisfactionclient.Service.EnqueteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/enquetes")
public class EnqueteController {

    @Autowired
    private EnqueteService enqueteService;
    @Autowired
    private UserRepository userRepository;
    // Créer une enquête avec ses questions
    public EnqueteController(EnqueteService enqueteService, UserRepository userRepository) {
        this.enqueteService = enqueteService;
        this.userRepository = userRepository;
    }

    // Méthode de création d'enquête
    @PostMapping("/create")
    public ResponseEntity<Enquete> creerEnqueteAvecQuestions(@RequestBody Enquete enquete) {
        // Vérifier si l'admin est valide
        User admin = userRepository.findById(enquete.getAdmin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Convertir la date de expiration depuis String vers LocalDateTime
        LocalDateTime expirationDate = enquete.getDateExpiration();

        // Appeler le service pour créer l'enquête avec les questions
        Enquete savedEnquete = enqueteService.creerEnqueteAvecQuestions(
                enquete.getTitre(),
                enquete.getDescription(),
                expirationDate,
                admin,
                enquete.getQuestions()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnquete);
    }
    // Publier une enquête
    @PostMapping("/publish/{id}")
    public ResponseEntity<Enquete> publierEnquete(@PathVariable Long id) {
        Enquete enquete = enqueteService.publierEnquete(id);
        return ResponseEntity.ok(enquete);
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
