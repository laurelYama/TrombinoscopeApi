package com.esiitech.trombinoscope_api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByResetToken(String resetToken);

}
