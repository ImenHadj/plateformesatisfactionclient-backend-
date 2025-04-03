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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReponse typeReponse; // Enum avec TEXTE, CHOIX, NUMERIQUE, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private Enquete enquete;

    @OneToOne(mappedBy = "reponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReponseTexte reponseTexte;

    @OneToMany(mappedBy = "reponse", cascade = CascadeType.ALL)
    private List<ReponseChoix> reponsesChoix = new ArrayList<>();

    // Getter pour la liste
    public List<ReponseChoix> getReponsesChoix() {
        return reponsesChoix;
    }

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

    public TypeReponse getTypeReponse() {
        return typeReponse;
    }

    public void setTypeReponse(TypeReponse typeReponse) {
        this.typeReponse = typeReponse;
    }

    public void setReponsesChoix(List<ReponseChoix> reponsesChoix) {
        this.reponsesChoix = reponsesChoix;
    }

    public ReponseTexte getReponseTexte() {
        return reponseTexte;
    }

    public void setReponseTexte(ReponseTexte reponseTexte) {
        this.reponseTexte = reponseTexte;
    }
}