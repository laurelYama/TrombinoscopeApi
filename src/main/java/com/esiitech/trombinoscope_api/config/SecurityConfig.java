package com.esiitech.trombinoscope_api.config;

import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import com.esiitech.trombinoscope_api.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UtilisateurRepository utilisateurRepository; //Injection du repository

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Activation des CORS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll() // Connexion accessible à tous
                        .requestMatchers("/api/auth/change-password").permitAll()
                        .requestMatchers("/api/utilisateurs/mot-de-passe-oublie", "/api/utilisateurs/reinitialiser-mot-de-passe").permitAll() // Permet l'accès sans authentification
                        .requestMatchers("/api/auth/register").hasRole("ADMIN") // Seul un ADMIN peut inscrire un utilisateur
                        .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN") // Protection des routes utilisateurs
                        .requestMatchers("/api/promotions/**").hasRole("ADMIN")
                        .requestMatchers("/api/specialites/**").hasRole("ADMIN")
                        .requestMatchers("/api/parcours/**").hasRole("ADMIN")
                        .requestMatchers("/api/diplomes/**").hasRole("ADMIN")
                        .requestMatchers("/api/etudiants/**").hasAnyRole("ADMIN", "NORMAL")
                        .requestMatchers("/api/photos/**").hasAnyRole("ADMIN", "NORMAL")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll() // Autoriser l'accès à Swagger
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new PasswordChangeFilter(utilisateurRepository), UsernamePasswordAuthenticationFilter.class); // ✅ Suppression du ; avant cette ligne

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
