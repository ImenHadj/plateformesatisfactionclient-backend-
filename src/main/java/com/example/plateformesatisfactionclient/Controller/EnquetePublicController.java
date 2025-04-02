package com.example.plateformesatisfactionclient.Controller;
import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.example.plateformesatisfactionclient.DTO.EnqueteResponseDTO;
import com.example.plateformesatisfactionclient.DTO.QuestionDTO;
import com.example.plateformesatisfactionclient.Entity.Enquete;
import com.example.plateformesatisfactionclient.Entity.Question;
import com.example.plateformesatisfactionclient.Entity.Reponse;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import com.example.plateformesatisfactionclient.Repository.QuestionRepository;
import com.example.plateformesatisfactionclient.Service.EnqueteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/enquete")
public class EnquetePublicController {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EnqueteRepository enqueteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnqueteService enqueteService;

    @PreAuthorize("permitAll()")
    @GetMapping("respond/{id}")
    public ResponseEntity<EnqueteResponseDTO> getEnqueteWithQuestions(@PathVariable Long id) {
        Enquete enquete = enqueteService.getEnqueteWithQuestions(id);

        EnqueteResponseDTO enqueteResponseDTO = new EnqueteResponseDTO();
        enqueteResponseDTO.setTitre(enquete.getTitre());
        enqueteResponseDTO.setDescription(enquete.getDescription());

        List<QuestionDTO> questionDTOs = enquete.getQuestions().stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId()); // Ajout de l'ID
                    dto.setTexte(question.getTexte());
                    dto.setType(question.getType());
                    //dto.setOptions(question.getOptions()); // Si vous avez des options
                    return dto;
                })
                .collect(Collectors.toList());

        enqueteResponseDTO.setQuestions(questionDTOs);

        return ResponseEntity.ok(enqueteResponseDTO);
    }
   /* @PostMapping("/respond/{enqueteId}")
    public ResponseEntity<String> repondreEnquete(@PathVariable Long enqueteId, @RequestParam Long userId, @RequestBody List<Reponse> reponses) {
        try {
            enqueteService.enregistrerReponses(enqueteId, userId, reponses);
            return ResponseEntity.ok("Réponses soumises avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur : " + e.getMessage());  // Retourner une erreur 404 si l'enquête ou l'utilisateur est introuvable
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }*/

    @PermitAll // Ajoutez cette annotation si l'endpoint doit être public

    @PostMapping("/respond/{enqueteId}")
    public ResponseEntity<String> repondreEnquete(
            @PathVariable Long enqueteId,
            @RequestParam Long userId,
            @RequestBody List<Reponse> reponses) {

        try {
            // 1. Validation des entrées
            if (reponses == null || reponses.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucune réponse à enregistrer");
            }

            // 2. Vérification des IDs de question
            for (Reponse reponse : reponses) {
                if (reponse.getQuestion() == null || reponse.getQuestion().getId() == null || reponse.getQuestion().getId() <= 0) {
                    return ResponseEntity.badRequest().body("ID de question invalide détecté");
                }
            }

            // 3. Vérification de l'enquête et de l'utilisateur
            Enquete enquete = enqueteRepository.findById(enqueteId)
                    .orElseThrow(() -> new RuntimeException("Enquête non trouvée"));

            User utilisateur = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // 4. Vérification des questions
            for (Reponse reponse : reponses) {
                Question question = questionRepository.findById(reponse.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("Question non trouvée"));

                if (!question.getEnquete().getId().equals(enqueteId)) {
                    return ResponseEntity.badRequest().body("La question ID " + question.getId() + " n'appartient pas à cette enquête");
                }
            }

            // 5. Enregistrement
            enqueteService.enregistrerReponses(enqueteId, userId, reponses);
            return ResponseEntity.ok("Réponses soumises avec succès !");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur: " + e.getMessage());
        }
    }



}

