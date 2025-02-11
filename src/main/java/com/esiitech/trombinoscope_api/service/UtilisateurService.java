package com.esiitech.trombinoscope_api.service;


import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword())); // Hash du mot de passe
        utilisateur.setForcePasswordChange(true); // Forcer le changement de mot de passe
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur modifierUtilisateur(Long id, Utilisateur updatedUser) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (updatedUser.getEmail() != null) {
            utilisateur.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getUsername() != null) {
            utilisateur.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            utilisateur.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur changerMotDePasse(Long id, String newPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        utilisateur.setForcePasswordChange(false); // Désactiver l'obligation de changer le mot de passe

        return utilisateurRepository.save(utilisateur);
    }


}
