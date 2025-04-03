package com.example.plateformesatisfactionclient.DTO;

import com.example.plateformesatisfactionclient.Entity.TypeReponse;

public class ReponseDTO {
    private Long questionId;
    private TypeReponse typeReponse; // Coherent avec l'entité
    private String texteReponse;     // Renommé pour être plus clair
    private String choixReponse;     // Renommé pour être plus clair

    public Long getQuestionId() {
        return questionId;
    }

    public TypeReponse getTypeReponse() {
        return typeReponse;
    }

    public String getTexteReponse() {
        return texteReponse;
    }

    public String getChoixReponse() {
        return choixReponse;
    }
}