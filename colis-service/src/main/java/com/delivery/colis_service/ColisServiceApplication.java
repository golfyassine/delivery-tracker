package com.delivery.colis_service;

import com.delivery.colis_service.entities.Colis; // Vérifiez si c'est 'entities' ou 'entites' chez vous
import com.delivery.colis_service.repository.ColisRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ColisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ColisServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(ColisRepository colisRepository) {
		return args -> {
			// 👇 J'ai ajouté celui-ci (numéro "1") pour que votre lien de test fonctionne !
			colisRepository.save(new Colis(null, "1", "Casablanca, Maroc", "Rabat, Maroc", "EN_COURS"));

			// Vos autres exemples
			colisRepository.save(new Colis(null, "A123", "Paris, France", "Rabat, Maroc", "LIVRÉ"));
			colisRepository.save(new Colis(null, "B456", "New York, USA", "Rabat, Maroc", "EN_ATTENTE"));

			System.out.println("✅ Données de test insérées avec succès !");
		};
	}
}