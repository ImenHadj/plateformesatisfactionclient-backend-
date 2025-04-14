package com.example.plateformesatisfactionclient.DTO;

import com.example.plateformesatisfactionclient.Entity.Question;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;
public class EnqueteResponseDTO {

    private String titre;
    private String description;
    private List<QuestionDTO> questions; // Utiliser une liste de QuestionDTO

    // Getters et setters

    private LocalDateTime dateExpiration; // ✅ Ajout ici

    // Getters et setters
    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}

