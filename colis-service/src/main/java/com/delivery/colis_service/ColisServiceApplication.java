package com.delivery.colis_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ColisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ColisServiceApplication.class, args);
	}

	// On a supprimé le CommandLineRunner ici.
	// Plus de données "en dur", tout passe par ton Angular !
}