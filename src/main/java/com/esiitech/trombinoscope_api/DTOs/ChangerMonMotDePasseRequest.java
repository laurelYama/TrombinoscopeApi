package com.esiitech.trombinoscope_api.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangerMonMotDePasseRequest {
    @NotBlank(message = "L'ancien mot de passe est requis.")
    private String ancienMotDePasse;

    @NotBlank(message = "Le nouveau mot de passe est requis.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    private String nouveauMotDePasse;


    public @NotBlank(message = "L'ancien mot de passe est requis.") String getAncienMotDePasse() {
        return ancienMotDePasse;
    }

    public void setAncienMotDePasse(@NotBlank(message = "L'ancien mot de passe est requis.") String ancienMotDePasse) {
        this.ancienMotDePasse = ancienMotDePasse;
    }

    public @NotBlank(message = "Le nouveau mot de passe est requis.") @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.") String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(@NotBlank(message = "Le nouveau mot de passe est requis.") @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.") String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }
}

