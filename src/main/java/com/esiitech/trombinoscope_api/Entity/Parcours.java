package com.esiitech.trombinoscope_api.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parcours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 45)
    @NotBlank(message = "Le niveau ne peut pas être vide")
    @Size(min = 2, max = 45, message = "Le niveau doit contenir entre 2 et 45 caractères")
    private String niveau;
}
