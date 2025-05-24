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

import com.affiliate.util.JwtRequestFilter;

import java.util.Arrays;
import java.util.Collections;

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

//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.csrf((csrf) -> csrf.disable())
//				.authorizeHttpRequests((requests) -> requests.requestMatchers("/api/auth/**").permitAll()
//						.requestMatchers("/api/influencer/profile").authenticated() // Requires authentication for /api/home/**
//					    .requestMatchers("/api/influencer/**").authenticated()
//				        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/home/**").permitAll() 
//                        .requestMatchers("/api/influencers").permitAll()
//                        .requestMatchers("/api/products").permitAll()
//                        .requestMatchers("/api/users/follow/**").authenticated()
//                        .requestMatchers("/api/users/unfollow/**").authenticated()
//                        .requestMatchers("/uploads/**").permitAll()// Secure this endpoint
//						.anyRequest().authenticated())
//				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No
//																												// session
//																												// needed
//																												// for
//																												// JWT
//				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Add your JWT filter
//				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
//				.logout((logout) -> logout.permitAll());
//
//		return http.build();
//	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/influencer").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/influencer/{id}").permitAll()
                .requestMatchers("/api/users/me").authenticated() 
				.requestMatchers("/api/influencer/profile").authenticated() 
			    .requestMatchers("/api/influencer/**").authenticated()
		        .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/home/**").permitAll() 
                .requestMatchers("/api/users/follow/**").permitAll()
                .requestMatchers("/api/users/unfollow/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .anyRequest().permitAll() // All other requests require authentication
            )
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.logout((logout) -> logout.permitAll());
        return http.build();
    }

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*")); // Allow requests from this origin
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP
																									// methods
		configuration.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
		configuration.setAllowCredentials(true); // Allow sending credentials (like cookies)
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
		return source;
	}
}