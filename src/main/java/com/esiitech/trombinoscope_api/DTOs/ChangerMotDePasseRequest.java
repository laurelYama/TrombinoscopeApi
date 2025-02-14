package com.esiitech.trombinoscope_api.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangerMotDePasseRequest {
    private String ancienMotDePasse;
    private String nouveauMotDePasse;
}
