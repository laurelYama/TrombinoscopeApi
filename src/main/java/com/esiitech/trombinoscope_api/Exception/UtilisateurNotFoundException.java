package com.esiitech.trombinoscope_api.Exception;

public class UtilisateurNotFoundException extends RuntimeException {
    public UtilisateurNotFoundException(Long id) {
        super("Utilisateur avec ID " + id + " non trouvé");
    }

    public UtilisateurNotFoundException(String message) {
        super(message);
    }
}

