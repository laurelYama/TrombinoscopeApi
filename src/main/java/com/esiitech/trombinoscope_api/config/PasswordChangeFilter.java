package com.esiitech.trombinoscope_api.config;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.Optional;

public class PasswordChangeFilter extends GenericFilterBean {

    private final UtilisateurRepository utilisateurRepository;

    public PasswordChangeFilter(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Vérifier si l'utilisateur est authentifié
        if (httpRequest.getUserPrincipal() == null) {
            chain.doFilter(request, response);
            return;
        }

        String email = httpRequest.getUserPrincipal().getName();

        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);

        if (utilisateur.isPresent() && utilisateur.get().isForcePasswordChange()) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez changer votre mot de passe.");
            return;
        }

        chain.doFilter(request, response);
    }

}

