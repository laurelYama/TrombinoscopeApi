package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentative de connexion avec un email inconnu: {}", email);
                    return new UsernameNotFoundException("Aucun utilisateur trouvé avec cet email");
                });

        log.info("Utilisateur authentifié: {}", email);

        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword()) // Assurez-vous que le mot de passe est encodé
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())))
                .accountLocked(!utilisateur.isAccountNonLocked()) // Supposons que `isAccountNonLocked()` existe
                .disabled(!utilisateur.isEnabled()) // Supposons que `isEnabled()` existe
                .build();
    }
}
