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
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Auth & Password
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers("/api/auth/change-password").permitAll()
            .requestMatchers("/api/utilisateurs/mot-de-passe-oublie", "/api/utilisateurs/reinitialiser-mot-de-passe").permitAll()

            // Swagger
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

            // Public GET for trombinoscope
            .requestMatchers(HttpMethod.GET, "/api/etudiants/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/photos/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/promotions/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/specialites/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/parcours/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/etudiant-promo/**").permitAll()

            // ADMIN protected routes
            .requestMatchers("/api/auth/register").hasRole("ADMIN")
            .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/promotions/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/promotions/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/specialites/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/specialites/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/parcours/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/parcours/**").hasRole("ADMIN")

            // ADMIN or NORMAL for managing (non-public)
            .requestMatchers(HttpMethod.POST, "/api/etudiants/**").hasAnyRole("ADMIN", "NORMAL")
            .requestMatchers(HttpMethod.PUT, "/api/etudiants/**").hasAnyRole("ADMIN", "NORMAL")
            .requestMatchers(HttpMethod.POST, "/api/photos/**").hasAnyRole("ADMIN", "NORMAL")
            .requestMatchers(HttpMethod.POST, "/api/etudiant-promo/**").hasAnyRole("ADMIN", "NORMAL")
            .requestMatchers(HttpMethod.PUT, "/api/etudiant-promo/**").hasAnyRole("ADMIN", "NORMAL")

            // All other requests need authentication
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new PasswordChangeFilter(utilisateurRepository), UsernamePasswordAuthenticationFilter.class);

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
