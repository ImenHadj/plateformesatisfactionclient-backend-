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




    @Scheduled(fixedRate = 60000) // Vérification toutes les minutes
    public void checkAndPublishEnquetes() {
        LocalDateTime now = LocalDateTime.now();

        List<Enquete> enquetesToPublish = enqueteRepository.findAllByDatePublicationBeforeAndStatut(now, StatutEnquete.BROUILLON);

        for (Enquete enquete : enquetesToPublish) {
            enquete.setStatut(StatutEnquete.PUBLIEE);
            enqueteRepository.save(enquete);

            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            for (User client : clients) {
                String enqueteLink = "http://localhost:5173/enquete/respond/" + enquete.getId() + "?userId=" + client.getId();
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink);
            }
        }
    }

}

