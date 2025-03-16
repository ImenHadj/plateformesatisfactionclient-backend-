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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texte; // Ex: "Que pensez-vous du service bancaire ?"

    @Enumerated(EnumType.STRING)
    private TypeQuestion type; // OUVERT, CHOIX_SIMPLE, CHOIX_MULTIPLE

    @ElementCollection
    private List<String> options = new ArrayList<>(); // Pour stocker les choix possibles

    @ManyToOne
    @JoinColumn(name = "enquete_id")
    private Enquete enquete;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reponse> reponses = new ArrayList<>();
    public void setTexte(String texte) {
        this.texte = texte;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setType(TypeQuestion type) {
        this.type = type;
    }

    public void setEnquete(Enquete enquete) {
        this.enquete = enquete;
    }
}

