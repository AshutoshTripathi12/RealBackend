//package com.affiliate.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig {
//
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/api/**") // Apply CORS to paths starting with /api/
//						.allowedOrigins("http://localhost:3000").allowedMethods("GET", "POST", "PUT", "DELETE") // Specify
//																												// allowed
//																												// HTTP
//																												// methods
//						.allowedHeaders("*"); // Allow all headers
//			}
//		};
//	}
//}