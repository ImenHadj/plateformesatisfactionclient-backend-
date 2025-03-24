package com.example.plateformesatisfactionclient.Repository;


import com.example.plateformesatisfactionclient.Entity.Enquete;
import com.example.plateformesatisfactionclient.Entity.StatutEnquete;
import com.example.plateformesatisfactionclient.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EnqueteRepository extends JpaRepository<Enquete, Long> {
    List<Enquete> findByAdmin(User admin);  // Trouver les enquêtes d'un admin
    List<Enquete> findByStatutAndDateExpirationAfter(String statut, LocalDateTime date);  // Trouver les enquêtes publiées qui ne sont pas expirées
    List<Enquete> findAllByDatePublicationBeforeAndStatut(LocalDateTime date, StatutEnquete statut);

}

