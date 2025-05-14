package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.ModifierUtilisateurRequest;
import com.esiitech.trombinoscope_api.DTOs.UtilisateurDto;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.UtilisateurNotFoundException;
import com.esiitech.trombinoscope_api.mapper.UtilisateurMapper;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UtilisateurMapper utilisateurMapper;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              UtilisateurMapper utilisateurMapper,
                              PasswordEncoder passwordEncoder,
                              EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<UtilisateurDto> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurMapper.toDtoList(utilisateurs);
    }

    public UtilisateurDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return utilisateurMapper.toDto(utilisateur);
    }

    public UtilisateurDto modifierUtilisateur(Long id, ModifierUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));

        if (request.getEmail() != null && !request.getEmail().equals(utilisateur.getEmail())) {
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
            }
            utilisateur.setEmail(request.getEmail());
        }

        if (request.getUsername() != null) {
            utilisateur.setUsername(request.getUsername());
        }

        if (request.getRole() != null) {
            utilisateur.setRole(request.getRole());
        }

        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(request.getMotDePasse()));
        }

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toDto(savedUser);
    }


    public UtilisateurDto getMonProfil() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        return utilisateurMapper.toDto(utilisateur);
    }

    public UtilisateurDto modifierMonProfil(String nouveauUsername, String nouveauMotDePasse) {
        Utilisateur utilisateur = getUtilisateurConnecte();

        if (nouveauUsername != null && !nouveauUsername.isEmpty()) {
            utilisateur.setUsername(nouveauUsername);
        }

        if (nouveauMotDePasse != null && !nouveauMotDePasse.isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
            utilisateur.setForcePasswordChange(false);
        }

        utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toDto(utilisateur);
    }

    private Utilisateur getUtilisateurConnecte() {
        String email = getEmailUtilisateurConnecte();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UtilisateurNotFoundException("Utilisateur non trouvé"));
    }

    private String getEmailUtilisateurConnecte() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public void demandeReinitialisationMotDePasse(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UtilisateurNotFoundException("Utilisateur non trouvé avec cet email"));

        String token = UUID.randomUUID().toString();
        utilisateur.setResetToken(token);
        utilisateur.setResetTokenTimestamp(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        String resetLink = "http://localhost:4200/reinitialiser-mot-de-passe?token=" + token;

        emailService.sendSimpleMessage(utilisateur.getEmail(), "Réinitialisation du mot de passe",
                "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink);
    }

    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByResetToken(token)
                .orElseThrow(() -> new UtilisateurNotFoundException("Token invalide ou expiré"));

        if (utilisateur.getResetTokenTimestamp() != null && isTokenExpired(utilisateur.getResetTokenTimestamp())) {
            throw new IllegalArgumentException("Le token de réinitialisation a expiré");
        }

        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setResetToken(null);
        utilisateur.setResetTokenTimestamp(null);
        utilisateur.setForcePasswordChange(true);
        utilisateurRepository.save(utilisateur);
    }

    private boolean isTokenExpired(LocalDateTime resetTokenTimestamp) {
        return Duration.between(resetTokenTimestamp, LocalDateTime.now()).toMinutes() > 30;
    }

    public List<UtilisateurDto> getUtilisateursActifs() {
        return utilisateurMapper.toDtoList(utilisateurRepository.findByActifTrue());
    }

    public void desactiverUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    public void activerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }

    public boolean doitChangerMotDePasse() {
        Utilisateur utilisateur = getUtilisateurConnecte(); // méthode existante dans ton service
        return utilisateur.isForcePasswordChange();
    }

    public void changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur utilisateur = getUtilisateurConnecte();

        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }

        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setForcePasswordChange(false); // Il ne sera plus obligé de changer le mot de passe au login
        utilisateurRepository.save(utilisateur);
    }

    public void supprimerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UtilisateurNotFoundException(id));
        utilisateurRepository.delete(utilisateur);
    }

    public List<UtilisateurDto> rechercherParUsernameOuEmail(String recherche) {
        List<Utilisateur> utilisateurs = utilisateurRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(recherche, recherche);

        if (utilisateurs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun utilisateur trouvé pour : " + recherche);
        }

        return utilisateurMapper.toDtoList(utilisateurs);
    }



}
