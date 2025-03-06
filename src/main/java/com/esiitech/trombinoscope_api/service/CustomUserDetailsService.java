package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.ApiException;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Récupération de l'utilisateur depuis la base de données
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouvé avec cet email"));

        // Vérification si l'utilisateur doit changer son mot de passe
        if (utilisateur.isForcePasswordChange()) {
            log.warn("L'utilisateur {} doit changer son mot de passe avant de se connecter.", email);
            throw new ApiException("L'utilisateur doit changer son mot de passe avant de pouvoir se connecter.", HttpStatus.FORBIDDEN);
        }

        // Log d'information pour l'authentification
        log.info("L'utilisateur {} est en cours de connexion.", utilisateur.getEmail());

        // Retour des détails de l'utilisateur
        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())))
                .accountLocked(!utilisateur.isAccountNonLocked())
                .disabled(!utilisateur.isEnabled())
                .build();
    }
}
