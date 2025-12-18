package com.security.security_service;

import com.security.security_service.entities.Role;
import com.security.security_service.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UserService userService) {
		return args -> {
			// On vérifie si on peut créer l'admin (pour éviter les doublons au redémarrage)
			try {
				userService.registerUser("admin", "1234", Role.ADMIN);
				System.out.println("✅ Utilisateur de test créé : admin / 1234");
			} catch (Exception e) {
				System.out.println("ℹ️ L'utilisateur admin existe déjà.");
			}
		};
	}
}