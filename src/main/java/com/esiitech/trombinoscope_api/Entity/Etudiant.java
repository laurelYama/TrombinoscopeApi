package com.esiitech.trombinoscope_api.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String genre;
    private String telephone;
    private String nationalite;
    private String photo;

    @ManyToOne
    @JoinColumn(name = "idPromotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "idParcours")
    private Parcours parcours;

    @ManyToOne
    @JoinColumn(name = "idSpecialite")
    private Specialite specialite;
}

