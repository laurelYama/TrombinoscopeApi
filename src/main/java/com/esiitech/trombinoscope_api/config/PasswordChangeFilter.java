package com.esiitech.trombinoscope_api.config;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class PasswordChangeFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(PasswordChangeFilter.class);
    private final UtilisateurRepository utilisateurRepository;

    // Constructeur avec injection du repository
    public PasswordChangeFilter(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Vérification si l'utilisateur est authentifié
        String email = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;

        if (email == null) {
            filterChain.doFilter(request, response);
            return; // Si l'utilisateur n'est pas authentifié, on passe au filtre suivant
        }

        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(email);

        // Si l'utilisateur existe et qu'il doit changer son mot de passe
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();

            if (utilisateur.isForcePasswordChange()) {
                // Si l'utilisateur doit changer son mot de passe, envoyer une erreur
                logger.warn("L'utilisateur {} doit changer son mot de passe.", email);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez changer votre mot de passe.");
                return; // Bloquer l'accès tant que le mot de passe n'est pas changé
            }
        }

        filterChain.doFilter(request, response); // Passer à la suite du filtrage
    }
}
