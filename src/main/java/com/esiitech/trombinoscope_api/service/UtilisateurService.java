package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.MotDePasseIncorrectException;
import com.esiitech.trombinoscope_api.Exception.UtilisateurNotFoundException;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        return utilisateurRepository.save(utilisateur);
    }

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

    public Utilisateur changerMotDePasse(Long id, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getPassword())) {
            throw new MotDePasseIncorrectException();
        }

        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        return utilisateurRepository.save(utilisateur);
    }
}
