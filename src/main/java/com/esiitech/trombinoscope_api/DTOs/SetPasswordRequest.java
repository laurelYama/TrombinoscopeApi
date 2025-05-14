package com.esiitech.trombinoscope_api.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPasswordRequest {
    @NotBlank
    private String token;
    @NotBlank
    private String nouveauMotDePasse;
    @NotBlank
    private String confirmationMotDePasse;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }

    public @NotBlank String getConfirmationMotDePasse() {
        return confirmationMotDePasse;
    }

    public void setConfirmationMotDePasse(@NotBlank String confirmationMotDePasse) {
        this.confirmationMotDePasse = confirmationMotDePasse;
    }
}

