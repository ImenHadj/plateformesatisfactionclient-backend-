package com.example.plateformesatisfactionclient.Controller;
import com.example.plateformesatisfactionclient.DTO.ReponseDTO;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
        enqueteResponseDTO.setDateExpiration(enquete.getDateExpiration()); // ✅ Ajout ici

        List<QuestionDTO> questionDTOs = enquete.getQuestions().stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId()); // Ajout de l'ID
                    dto.setTexte(question.getTexte());
                    dto.setType(question.getType());
                    dto.setOptions(question.getOptions()); // Si vous avez des options
                    return dto;
                })
                .collect(Collectors.toList());

        enqueteResponseDTO.setQuestions(questionDTOs);

        return ResponseEntity.ok(enqueteResponseDTO);
    }



    @PermitAll

    @PostMapping("/respond/{enqueteId}")
    public ResponseEntity<String> repondreEnquete(
            @PathVariable Long enqueteId,
            @RequestParam Long userId,
            @RequestBody List<ReponseDTO> reponsesDTO) {
        try {
            enqueteService.enregistrerReponses(enqueteId, userId, reponsesDTO);
            return ResponseEntity.ok("Réponses enregistrées");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}

