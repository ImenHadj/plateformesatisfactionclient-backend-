package com.example.plateformesatisfactionclient.Service;

import com.example.plateformesatisfactionclient.Entity.*;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import com.example.plateformesatisfactionclient.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnqueteService {

    @Autowired
    private EnqueteRepository enqueteRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public EnqueteService(EnqueteRepository enqueteRepository) {
        this.enqueteRepository = enqueteRepository;
    }

    // Création d'enquête avec questions
    public Enquete creerEnqueteAvecQuestions(String titre, String description, LocalDateTime dateExpiration, User admin, List<Question> questions) {
        // Création d'une nouvelle enquête
        Enquete enquete = new Enquete();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateCreation(LocalDateTime.now());
        enquete.setDateExpiration(dateExpiration);
        enquete.setStatut(StatutEnquete.BROUILLON);  // Statut initial de l'enquête
        enquete.setAdmin(admin);

        // Associer chaque question à l'enquête
        for (Question question : questions) {
            question.setEnquete(enquete);  // Associe chaque question à l'enquête
        }

        enquete.setQuestions(questions);  // Ajouter les questions à l'enquête

        // Enregistrer l'enquête et ses questions dans la base de données
        return enqueteRepository.save(enquete);  // Cela persistera l'enquête et ses questions grâce à CascadeType.ALL
    }
    // Publier une enquête
    public Enquete publierEnquete(Long enqueteId) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        enquete.setStatut(StatutEnquete.PUBLIEE);
        return enqueteRepository.save(enquete);
    }

    // Modifier une enquête
    public Enquete modifierEnquete(Long enqueteId, String titre, String description, LocalDateTime dateExpiration) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateExpiration(dateExpiration);
        return enqueteRepository.save(enquete);
    }

    // Supprimer une enquête
    public void supprimerEnquete(Long enqueteId) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        enqueteRepository.delete(enquete);
    }

    // Ajouter une question à une enquête
    public Question ajouterQuestion(Long enqueteId, String texte, List<String> options, TypeQuestion type) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        Question question = new Question();
        question.setTexte(texte);
        question.setOptions(options);
        question.setType(type);
        question.setEnquete(enquete);

        return questionRepository.save(question);
    }

    // Obtenir les enquêtes créées par un admin
    public List<Enquete> getEnquetesAdmin(User admin) {
        return enqueteRepository.findByAdmin(admin);
    }

    // Obtenir les questions d'une enquête
    public List<Question> getQuestionsForEnquete(Long enqueteId) {
        return questionRepository.findByEnqueteId(enqueteId);
    }
}
