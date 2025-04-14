package com.example.plateformesatisfactionclient.Service;

import com.example.plateformesatisfactionclient.DTO.ReponseDTO;
import com.example.plateformesatisfactionclient.Entity.*;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import com.example.plateformesatisfactionclient.Repository.QuestionRepository;
import com.example.plateformesatisfactionclient.Repository.ReponseRepository;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnqueteService {

    @Autowired
    private EnqueteRepository enqueteRepository;

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private Authservice userService;

    @Autowired
    private Emailservice emailService;
    @Autowired
    private ReponseRepository reponseRepository;
    @Autowired
    private UserRepository userRepository;

    public EnqueteService(EnqueteRepository enqueteRepository) {
        this.enqueteRepository = enqueteRepository;
    }


    public Enquete creerEnqueteAvecQuestionsEtOptions(String titre, String description, LocalDateTime datePublication, LocalDateTime dateExpiration, User admin, List<Question> questions) {
        Enquete enquete = new Enquete();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateCreation(LocalDateTime.now());
        enquete.setDatePublication(datePublication);
        enquete.setDateExpiration(dateExpiration);
        enquete.setStatut(StatutEnquete.BROUILLON);
        enquete.setAdmin(admin);

        if (LocalDateTime.now().isAfter(datePublication) || LocalDateTime.now().isEqual(datePublication)) {
            enquete.setStatut(StatutEnquete.PUBLIEE);
        }

        for (Question question : questions) {
            question.setEnquete(enquete);
        }

        enquete.setQuestions(questions);

        Enquete savedEnquete = enqueteRepository.save(enquete);

        if (enquete.getStatut() == StatutEnquete.PUBLIEE) {
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);
            for (User client : clients) {
                String enqueteLink = "http://localhost:5173/enquete/respond/" + savedEnquete.getId() + "?userId=" + client.getId();
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink);
            }
        }

        return savedEnquete;
    }

    public Enquete getEnqueteWithQuestions(Long id) {
        return enqueteRepository.findById(id).map(enquete -> {
            enquete.getQuestions().forEach(question -> {
                if (question.getOptions() != null) {
                    question.getOptions().size();
                }
            });
            return enquete;
        }).orElseThrow(() -> new NoSuchElementException("Enquête non trouvée avec l'id : " + id));
    }

    // Modifier une enquête
    public Enquete modifierEnquete(Long enqueteId, String titre, String description, LocalDateTime dateExpiration) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateExpiration(dateExpiration);
        return enqueteRepository.save(enquete);
    }

    // Supprimer une enquête
    public void supprimerEnquete(Long enqueteId) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        enqueteRepository.delete(enquete);
    }

    public Question ajouterQuestion(Long enqueteId, String texte, List<String> options, TypeQuestion type) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        Question question = new Question();
        question.setTexte(texte);
        question.setOptions(options);
        question.setType(type);
        question.setEnquete(enquete);

        return questionRepository.save(question);
    }

    public List<Enquete> getEnquetesAdmin(User admin) {
        return enqueteRepository.findByAdmin(admin);
    }

    public List<Question> getQuestionsForEnquete(Long enqueteId) {
        return questionRepository.findByEnqueteId(enqueteId);
    }


    @Transactional
    public void enregistrerReponses(Long enqueteId, Long userId, List<ReponseDTO> reponsesDTO) {
        // Récupération de l'enquête
        Enquete enquete = enqueteRepository.findById(enqueteId)
                .orElseThrow(() -> new RuntimeException("Enquête non trouvée"));

        // Récupération de l'utilisateur
        User utilisateur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupération des réponses et conversion de chaque DTO en entité Reponse
        List<Reponse> reponses = reponsesDTO.stream()
                .map(dto -> convertirReponse(dto, enquete, utilisateur))
                .collect(Collectors.toList());

        // Sauvegarde des réponses dans la base de données
        reponseRepository.saveAll(reponses);
    }


    private Reponse convertirReponse(ReponseDTO dto, Enquete enquete, User utilisateur) {
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question non trouvée"));

        Reponse reponse = new Reponse();
        reponse.setTypeReponse(dto.getTypeReponse());
        reponse.setEnquete(enquete);
        reponse.setUser(utilisateur);
        reponse.setQuestion(question);

        switch (dto.getTypeReponse()) {
            case TEXTE:
            case EMAIL:
            case TELEPHONE:

            case CHOIX_COULEUR:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;

            case CHOIX_SIMPLE:
            case OUI_NON:
                if (dto.getChoixReponses() != null && !dto.getChoixReponses().isEmpty()) {
                    reponse.setValeursChoixFromList(List.of(dto.getChoixReponses().get(0)));
                }
                break;

            case CHOIX_MULTIPLE:
                if (dto.getChoixReponses() != null && !dto.getChoixReponses().isEmpty()) {
                    reponse.setValeursChoixFromList(dto.getChoixReponses());
                }
                break;

            case NUMERIQUE:
            case NOTE:
            case SLIDER:
            case POURCENTAGE:
            case DEVISE:
                if (dto.getValeurNumerique() != null) {
                    reponse.setValeurNumerique(dto.getValeurNumerique());
                } else if (dto.getTexteReponse() != null) {
                    try {
                        reponse.setValeurNumerique(Double.parseDouble(dto.getTexteReponse()));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Valeur numérique invalide : " + dto.getTexteReponse());
                    }
                }
                break;

            case DATE_HEURE:
            case DATE:
            case HEURE:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;

            case FICHIER:
            case IMAGE:

            case SIGNATURE:
            case DESSIN:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;


            case LOCALISATION:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;

            case MATRICE:
            case CLASSEMENT:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;

            case CAPTCHA:
            case QR_CODE:
            case CODE_PIN:
                reponse.setValeurTexte(dto.getTexteReponse());
                break;

            default:
                throw new RuntimeException("Type de réponse non supporté : " + dto.getTypeReponse());
        }

        return reponse;
    }
}