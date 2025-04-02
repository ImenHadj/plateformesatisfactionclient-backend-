package com.example.plateformesatisfactionclient.Repository;

import com.example.plateformesatisfactionclient.Entity.Question;
import com.example.plateformesatisfactionclient.Entity.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReponseRepository extends JpaRepository<Reponse, Long> {
}
