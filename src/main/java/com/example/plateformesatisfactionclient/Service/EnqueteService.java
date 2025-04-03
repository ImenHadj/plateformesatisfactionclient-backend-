package com.example.plateformesatisfactionclient.Service;

import com.example.plateformesatisfactionclient.DTO.ReponseDTO;
import com.example.plateformesatisfactionclient.Entity.*;
import com.example.plateformesatisfactionclient.Repository.EnqueteRepository;
import com.example.plateformesatisfactionclient.Repository.QuestionRepository;
import com.example.plateformesatisfactionclient.Repository.ReponseRepository;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EnqueteService {

    @Autowired
    private EnqueteRepository enqueteRepository;

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private Authservice userService;  // Service pour récupérer les utilisateurs

    @Autowired
    private Emailservice emailService;
    @Autowired
    private ReponseRepository reponseRepository;
    @Autowired
    private UserRepository userRepository;
    public EnqueteService(EnqueteRepository enqueteRepository) {
        this.enqueteRepository = enqueteRepository;
    }


   /* public Enquete creerEnqueteAvecQuestions(String titre, String description, LocalDateTime datePublication, LocalDateTime dateExpiration, User admin, List<Question> questions) {
        // Création d'une nouvelle enquête
        Enquete enquete = new Enquete();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateCreation(LocalDateTime.now());
        enquete.setDatePublication(datePublication);  // Date de publication spécifiée par l'utilisateur
        enquete.setDateExpiration(dateExpiration);    // Date d'expiration
        enquete.setStatut(StatutEnquete.BROUILLON);    // Statut initial de l'enquête
        enquete.setAdmin(admin);

        // Si la date de publication est atteinte, nous publions l'enquête
        if (LocalDateTime.now().isAfter(datePublication) || LocalDateTime.now().isEqual(datePublication)) {
            enquete.setStatut(StatutEnquete.PUBLIEE);  // Change le statut à "PUBLIEE" quand la date de publication est atteinte
        }

        // Associer chaque question à l'enquête
        for (Question question : questions) {
            question.setEnquete(enquete);
        }

        enquete.setQuestions(questions);  // Ajouter les questions à l'enquête

        // Enregistrer l'enquête et ses questions dans la base de données
        return enqueteRepository.save(enquete);
    }*/


    public Enquete creerEnqueteAvecQuestions(String titre, String description, LocalDateTime datePublication, LocalDateTime dateExpiration, User admin, List<Question> questions) {
        // Création d'une nouvelle enquête
        Enquete enquete = new Enquete();
        enquete.setTitre(titre);
        enquete.setDescription(description);
        enquete.setDateCreation(LocalDateTime.now());
        enquete.setDatePublication(datePublication);  // Date de publication spécifiée par l'utilisateur
        enquete.setDateExpiration(dateExpiration);    // Date d'expiration
        enquete.setStatut(StatutEnquete.BROUILLON);    // Statut initial de l'enquête
        enquete.setAdmin(admin);

        // Si la date de publication est atteinte, nous publions l'enquête
        if (LocalDateTime.now().isAfter(datePublication) || LocalDateTime.now().isEqual(datePublication)) {
            enquete.setStatut(StatutEnquete.PUBLIEE);  // Change le statut à "PUBLIEE" quand la date de publication est atteinte
        }

        // Associer chaque question à l'enquête
        for (Question question : questions) {
            question.setEnquete(enquete);
        }

        enquete.setQuestions(questions);  // Ajouter les questions à l'enquête

        // Enregistrer l'enquête et ses questions dans la base de données
        Enquete savedEnquete = enqueteRepository.save(enquete);

        // Vérifier si l'enquête a été publiée et envoyer le lien aux utilisateurs "CLIENT"
        if (enquete.getStatut() == StatutEnquete.PUBLIEE) {
            // Récupérer tous les utilisateurs ayant le rôle "CLIENT"
            List<User> clients = userService.getUsersByRole(ERole.ROLE_Client);

            // Envoyer le lien de l'enquête à chaque client
            String enqueteLink = "http://localhost:8080/enquete/respond/" + savedEnquete.getId();
            for (User client : clients) {
                emailService.sendEnqueteLink(client.getEmail(), enqueteLink); // Envoi du lien d'enquête
            }
        }

        return savedEnquete;
    }



    public Enquete getEnqueteWithQuestions(Long id) {
        Optional<Enquete> enqueteOpt = enqueteRepository.findById(id);
        if (enqueteOpt.isPresent()) {
            return enqueteOpt.get(); // Retourne l'enquête avec ses questions
        } else {
            throw new NoSuchElementException("Enquête non trouvée avec l'id : " + id);
        }}
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

    // Ajouter une question à une enquête
    public Question ajouterQuestion(Long enqueteId, String texte, List<String> options, TypeQuestion type) {
        Enquete enquete = enqueteRepository.findById(enqueteId).orElseThrow();
        Question question = new Question();
        question.setTexte(texte);
        question.setOptions(options);
        question.setType(type);
        question.setEnquete(enquete);

        return questionRepository.save(question);
    }

    // Obtenir les enquêtes créées par un admin
    public List<Enquete> getEnquetesAdmin(User admin) {
        return enqueteRepository.findByAdmin(admin);
    }

    // Obtenir les questions d'une enquête
    public List<Question> getQuestionsForEnquete(Long enqueteId) {
        return questionRepository.findByEnqueteId(enqueteId);
    }


  /* @Transactional
   public void enregistrerReponses(Long enqueteId, Long userId, List<Reponse> reponses) {
       // 1. Chargement des entités
       Enquete enquete = enqueteRepository.findById(enqueteId)
               .orElseThrow(() -> new RuntimeException("Enquête non trouvée"));

       User utilisateur = userRepository.findById(userId)
               .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

       // 2. Association des réponses
       for (Reponse reponse : reponses) {
           Question question = questionRepository.findById(reponse.getQuestion().getId())
                   .orElseThrow(() -> new RuntimeException("Question non trouvée"));

           if (!question.getEnquete().getId().equals(enqueteId)) {
               throw new RuntimeException("La question ID " + question.getId() + " n'appartient pas à cette enquête");
           }

           reponse.setEnquete(enquete);
           reponse.setUser(utilisateur);
           reponse.setQuestion(question);
       }

       // 3. Sauvegarde
       reponseRepository.saveAll(reponses);
   }*/

    @Transactional
    public void enregistrerReponses(Long enqueteId, Long userId, List<ReponseDTO> reponsesDTO) {
        Enquete enquete = enqueteRepository.findById(enqueteId)
                .orElseThrow(() -> new RuntimeException("Enquête non trouvée"));

        User utilisateur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvée"));

        List<Reponse> reponses = reponsesDTO.stream()
                .map(dto -> convertirReponse(dto, enquete, utilisateur))
                .toList();

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
                ReponseTexte rt = new ReponseTexte();
                rt.setValeur(dto.getTexteReponse());  // Utilise setValeur()
                rt.setReponse(reponse);  // Utilise setReponse()
                reponse.setReponseTexte(rt);  // Utilise setReponseTexte()
                break;

            case CHOIX_SIMPLE:
            case CHOIX_MULTIPLE:
                ReponseChoix rc = new ReponseChoix();
                rc.setValeurChoix(dto.getChoixReponse());
                rc.setReponse(reponse);
                rc.setQuestion(question);
                reponse.getReponsesChoix().add(rc);
                break;

            default:
                throw new RuntimeException("Type de réponse non supporté");
        }

        return reponse;
    }}
