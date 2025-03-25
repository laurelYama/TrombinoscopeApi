package com.esiitech.trombinoscope_api.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diplome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le numéro du diplôme est obligatoire")
    @Column(unique = true, nullable = false)
    private String numero;

    @NotBlank(message = "Le nom du diplôme est obligatoire")
    @Column(nullable = false)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonBackReference
    private Etudiant etudiant;
}
