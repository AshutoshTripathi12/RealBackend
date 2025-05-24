package com.affiliate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.affiliate.util.JwtRequestFilter; // Assuming this is your JWT filter's package

import java.util.Arrays;
import java.util.List; // Import List

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 🎯 Specify your Vercel frontend URL and localhost for development
        configuration.setAllowedOrigins(List.of(
                "https://influen-shop-backend-g5ge.vercel.app", // Your Vercel frontend
                "http://localhost:3000"  // For local React development (if using port 3000)
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Origin", "Accept")); // Added Origin and Accept
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // If you send custom headers back
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration first
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Crucially allow all OPTIONS requests (preflight)
                .requestMatchers(HttpMethod.GET, "/api/influencer").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/influencer/{id}").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/home/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // Keep other permitAll for public GETs as needed
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/influencer/profile").authenticated()
                .requestMatchers("/api/influencer/**").authenticated() // Might overlap with public GET if not specific
                .requestMatchers("/api/users/follow/**").authenticated() // Changed back to authenticated as per original
                .requestMatchers("/api/users/unfollow/**").authenticated() // Changed back to authenticated
                .anyRequest().authenticated() // Secure all other requests by default
            )
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout.permitAll());
        return http.build();
    }
}