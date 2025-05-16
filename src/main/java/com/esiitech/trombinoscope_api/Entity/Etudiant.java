package com.esiitech.trombinoscope_api.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le genre est obligatoire")
    private String genre;

    @Pattern(regexp = "\\d{9}", message = "Le numéro de téléphone doit contenir exactement 9 chiffres")
    @Column(nullable = false)
    private String telephone;


    @NotBlank(message = "La nationalité est obligatoire")
    private String nationalite;

    private String photoPath;

    private boolean actif = true;


    // Association avec les diplômes
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Diplome> diplomes = new ArrayList<>();
}
