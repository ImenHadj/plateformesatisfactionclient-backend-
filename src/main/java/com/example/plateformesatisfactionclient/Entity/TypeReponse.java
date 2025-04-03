package com.example.plateformesatisfactionclient.Entity;

public enum TypeReponse {
    // Correspondance avec TypeQuestion
    TEXTE,             // Pour OUVERT, TEXTE_LONG, EMAIL, URL, etc.
    CHOIX_SIMPLE,
    CHOIX_MULTIPLE,
    NUMERIQUE,        // Pour NOTE, LIKERT, SLIDER, POURCENTAGE, NUMERIQUE
    DATE_HEURE,        // Pour DATE, HEURE, DATE_HEURE
    FICHIER,           // Pour FICHIER, IMAGE, AUDIO, VIDEO, SIGNATURE
    GEO,               // Pour LOCALISATION
    MATRICE,           // Pour MATRICE, CLASSEMENT
    CODE,              // Pour CODE_PIN, CAPTCHA, QR_CODE
    COULEUR            // Pour CHOIX_COULEUR
}