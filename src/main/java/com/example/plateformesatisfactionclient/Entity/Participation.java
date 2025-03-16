package com.example.plateformesatisfactionclient.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "enquete_id"})
})
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateParticipation;


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User utilisateur;  // Changer "client" par "utilisateur"

    @ManyToOne
    @JoinColumn(name = "enquete_id", nullable = false)
    private Enquete enquete;


    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reponse> reponses = new ArrayList<>();
}

