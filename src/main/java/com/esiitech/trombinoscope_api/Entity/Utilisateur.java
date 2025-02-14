package com.esiitech.trombinoscope_api.Entity;

import com.esiitech.trombinoscope_api.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur implements UserDetails {  // <--- Implémente UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean forcePasswordChange = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== Implémentation des méthodes de UserDetails ==========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // Tu peux ajouter des rôles ici si nécessaire
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Modifier selon ta logique métier
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Modifier selon ta logique métier
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Modifier selon ta logique métier
    }

    @Override
    public boolean isEnabled() {
        return true;  // Modifier selon ta logique métier
    }


}
