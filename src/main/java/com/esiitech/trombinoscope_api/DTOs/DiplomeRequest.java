package com.esiitech.trombinoscope_api.DTOs;


import lombok.Data;

@Data
public class DiplomeRequest {
    private String numero;  // Numéro unique du diplôme
    private String nom;
    private Long etudiantId;
}

