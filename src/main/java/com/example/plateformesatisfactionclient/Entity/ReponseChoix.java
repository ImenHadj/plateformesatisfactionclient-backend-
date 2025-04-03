package com.example.plateformesatisfactionclient.Entity;

import jakarta.persistence.*;

@Entity
public class ReponseChoix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String valeurChoix; // Renommé pour être cohérent

    @ManyToOne
    @JoinColumn(name = "reponse_id")
    private Reponse reponse;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public void setValeurChoix(String valeurChoix) {
        this.valeurChoix = valeurChoix;
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}