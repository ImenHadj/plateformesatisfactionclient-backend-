package com.example.plateformesatisfactionclient.Service;

import com.example.plateformesatisfactionclient.Entity.ERole;
import com.example.plateformesatisfactionclient.Entity.Enquete;
import com.example.plateformesatisfactionclient.Entity.StatutEnquete;
import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class EnqueteScheduler {


    private final EnqueteRepository enqueteRepository;
    @Autowired
    private Authservice userService;  // Service pour récupérer les utilisateurs

    @Autowired
    private Emailservice emailService;
    public EnqueteScheduler(EnqueteRepository enqueteRepository) {
        this.enqueteRepository = enqueteRepository;
    }

    // Méthode planifiée qui s'exécute toutes les heures (ou selon l'intervalle que vous souhaitez)
   /* @Scheduled(fixedRate = 60000) // Exécute la tâche toutes les 60 secondes
    public void publierEnquetesAutomatiquement() {
        LocalDateTime now = LocalDateTime.now();

        // Rechercher toutes les enquêtes qui ont une date de publication passée et un statut "BROUILLON"
        List<Enquete> enquetes = enqueteRepository.findAllByDatePublicationBeforeAndStatut(
                now, StatutEnquete.BROUILLON);

        for (Enquete enquete : enquetes) {
            // Mettre à jour le statut de l'enquête
            enquete.setStatut(StatutEnquete.PUBLIEE);
            enqueteRepository.save(enquete);
        }
    }*/

    @Scheduled(fixedRate = 60000) // Vérification toutes les minutes
    public void checkAndPublishEnquetes() {
        LocalDateTime now = LocalDateTime.now();

        // Trouver les enquêtes dont la date de publication est passée mais qui ne sont pas encore publiées
        List<Enquete> enquetesToPublish = enqueteRepository.findAllByDatePublicationBeforeAndStatut(now, StatutEnquete.BROUILLON);

        for (Enquete enquete : enquetesToPublish) {
            // Mettre à jour le statut à "PUBLIEE"
            enquete.setStatut(StatutEnquete.PUBLIEE);
            enqueteRepository.save(enquete);

            // Envoyer l'email aux clients ayant le rôle "ROLE_CLIENT"
            String enqueteLink = "http://localhost:9090/enquete/respond/" + enquete.getId();
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            for (User client : clients) {
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink); // Envoi du lien d'enquête
            }
        }
    }
}

