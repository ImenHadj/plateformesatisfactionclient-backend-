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


    /*@Scheduled(fixedRate = 60000) // Vérification toutes les minutes
    public void checkAndPublishEnquetes() {
        LocalDateTime now = LocalDateTime.now();

        // Trouver les enquêtes dont la date de publication est passée mais qui ne sont pas encore publiées
        List<Enquete> enquetesToPublish = enqueteRepository.findAllByDatePublicationBeforeAndStatut(now, StatutEnquete.BROUILLON);

        for (Enquete enquete : enquetesToPublish) {
            // Mettre à jour le statut à "PUBLIEE"
            enquete.setStatut(StatutEnquete.PUBLIEE);
            enqueteRepository.save(enquete);

            // Envoyer l'email aux clients ayant le rôle "ROLE_CLIENT"
            String enqueteLink = "http://localhost:5173/enquete/respond/" + enquete.getId();
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            for (User client : clients) {
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink); // Envoi du lien d'enquête
            }
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
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            for (User client : clients) {
                // Inclure l'userId dans l'URL de l'enquête
                String enqueteLink = "http://localhost:5173/enquete/respond/" + enquete.getId() + "?userId=" + client.getId();
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink); // Envoi du lien d'enquête
            }
        }
    }

}

