package com.esiitech.trombinoscope_api.DTOs;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MotDePasseOublieRequest {
    @NotNull(message = "L'email est requis.")
    @Email(message = "Veuillez fournir une adresse email valide.")
    private String email;

}



