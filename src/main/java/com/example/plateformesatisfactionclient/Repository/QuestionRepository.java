package com.example.plateformesatisfactionclient.Repository;

import com.example.plateformesatisfactionclient.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByEnqueteId(Long enqueteId);  // Trouver les questions d'une enquête
}
