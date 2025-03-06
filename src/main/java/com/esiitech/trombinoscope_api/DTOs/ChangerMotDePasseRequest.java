package com.esiitech.trombinoscope_api.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangerMotDePasseRequest {

    @Email(message = "L'email doit être valide.")
    private String email;

    @NotBlank(message = "L'ancien mot de passe ne peut pas être vide.")
    private String ancienMotDePasse;

    @NotBlank(message = "Le nouveau mot de passe ne peut pas être vide.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    private String nouveauMotDePasse;
}
