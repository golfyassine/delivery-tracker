package com.delivery.colis_service.web;

import com.delivery.colis_service.entities.Colis;
import com.delivery.colis_service.repository.ColisRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colis") // 👈 L'adresse de base du vendeur
public class ColisController {

    private final ColisRepository colisRepository;

    public ColisController(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }

    // Répond à GET /api/colis/{numero}
    @GetMapping("/{numero}")
    public Colis getColisByNumero(@PathVariable String numero) {
        Colis colis = colisRepository.findByNumeroSuivi(numero);

        if (colis == null) {
            throw new RuntimeException("Colis introuvable avec le numéro : " + numero);
        }

        return colis;
    }
}