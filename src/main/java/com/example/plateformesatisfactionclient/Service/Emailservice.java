package com.example.plateformesatisfactionclient.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class Emailservice {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Réinitialisation de votre mot de passe");
            helper.setText("<p>Bonjour,</p>"
                            + "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>"
                            + "<p><a href='" + resetLink + "'>Réinitialiser mon mot de passe</a></p>"
                            + "<p>Si vous n'avez pas demandé de réinitialisation, ignorez cet email.</p>",
                    true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}
