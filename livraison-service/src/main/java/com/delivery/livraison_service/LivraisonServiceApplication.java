package com.delivery.livraison_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <--- Import

@SpringBootApplication
@EnableFeignClients // <--- AJOUTE CETTE LIGNE
public class LivraisonServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LivraisonServiceApplication.class, args);
	}
}