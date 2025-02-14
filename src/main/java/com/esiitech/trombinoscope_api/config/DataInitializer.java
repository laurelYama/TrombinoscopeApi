package com.esiitech.trombinoscope_api.config;

import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Enum.Role;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Optional<Utilisateur> existingAdmin = utilisateurRepository.findByEmail("admin@trombinoscope.com");

        if (existingAdmin.isEmpty()) {
            Utilisateur admin = Utilisateur.builder()
                    .username("admin")
                    .email("admin@trombinoscope.com")
                    .password(passwordEncoder.encode("admin123")) // Mot de passe encodé
                    .role(Role.ADMIN)
                    .forcePasswordChange(true)
                    .createdAt(LocalDateTime.now()) // Ajout de createdAt
                    .updatedAt(LocalDateTime.now()) // Ajout de updatedAt
                    .build();

            utilisateurRepository.save(admin);
            System.out.println("✅ Utilisateur ADMIN créé avec succès !");
        } else {
            System.out.println("ℹ️ L'utilisateur ADMIN existe déjà. Aucune création nécessaire.");
        }
    }
}
