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
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation; // Lien avec l'utilisateur et l'enquête

    private String reponseText; // Si la question est de type OUVERT

    @ElementCollection
    private List<String> choixSelectionnes = new ArrayList<>(); // Si la question est CHOIX_SIMPLE ou CHOIX_MULTIPLE
}

