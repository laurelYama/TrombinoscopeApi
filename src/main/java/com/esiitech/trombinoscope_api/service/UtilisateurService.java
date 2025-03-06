package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.UtilisateurNotFoundException;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));
    }

    public void supprimerUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new UtilisateurNotFoundException(id);
        }
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur modifierUtilisateur(Long id, Utilisateur updatedUser) {
        return utilisateurRepository.findById(id).map(utilisateur -> {
            utilisateur.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                utilisateur.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            return utilisateurRepository.save(utilisateur);
        }).orElseThrow(() -> new UtilisateurNotFoundException(id));
    }

    public void demandeReinitialisationMotDePasse(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UtilisateurNotFoundException("Utilisateur non trouvé avec cet email"));

        String token = UUID.randomUUID().toString();
        utilisateur.setResetToken(token);
        utilisateur.setResetTokenTimestamp(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        // Générer le lien de réinitialisation
        String resetLink = "http://localhost:8080/api/utilisateurs/reinitialiser-mot-de-passe?token=" + token;

        // Envoyer l'email avec le lien de réinitialisation
        emailService.sendSimpleMessage(utilisateur.getEmail(), "Réinitialisation du mot de passe",
                "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink);
    }

    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByResetToken(token)
                .orElseThrow(() -> new UtilisateurNotFoundException("Token invalide ou expiré"));

        // Vérification si le token est expiré
        if (utilisateur.getResetTokenTimestamp() != null && isTokenExpired(utilisateur.getResetTokenTimestamp())) {
            throw new IllegalArgumentException("Le token de réinitialisation a expiré");
        }

        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setResetToken(null); // Supprimer le token après utilisation
        utilisateur.setResetTokenTimestamp(null); // Supprimer également le timestamp
        utilisateurRepository.save(utilisateur);
    }

    // Méthode pour vérifier si le token est expiré (ici 30 minutes)
    private boolean isTokenExpired(LocalDateTime resetTokenTimestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(resetTokenTimestamp, now);
        long minutesElapsed = duration.toMinutes();  // Durée en minutes
        return minutesElapsed > 30;  // le token expire après 30 minutes
    }


}
