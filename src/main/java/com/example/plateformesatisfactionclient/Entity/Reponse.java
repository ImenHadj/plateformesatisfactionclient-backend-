package com.example.plateformesatisfactionclient.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // La question concernée

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // L'utilisateur qui a répondu

    private String reponseText; // Réponse si la question est de type OUVERT

    @ElementCollection
    private List<String> choixSelectionnes = new ArrayList<>(); // Réponses si la question est CHOIX_SIMPLE ou CHOIX_MULTIPLE
    @ManyToOne
    @JoinColumn(name = "enquete_id", nullable = false)
    private Enquete enquete;


    public void setEnquete(Enquete enquete) {
        this.enquete = enquete;
    }

    // Ajoutez le setter pour User
    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
