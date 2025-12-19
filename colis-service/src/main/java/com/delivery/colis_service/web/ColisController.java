package com.delivery.colis_service.web;

import com.delivery.colis_service.entities.Colis;
import com.delivery.colis_service.repository.ColisRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colis")
public class ColisController {

    private final ColisRepository colisRepository;

    public ColisController(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }

    // 1. AJOUTER UN COLIS (Reçoit les données d'Angular)
    @PostMapping("/ajouter")
    public Colis ajouterColis(@RequestBody Colis colis) {
        // Pas besoin de définir le statut ici, le @PrePersist de l'entité s'en occupe !
        return colisRepository.save(colis);
    }

    // 2. RÉCUPÉRER PAR ID (Utilisé par Livraison-Service)
    @GetMapping("/id/{id}")
    public Colis getColisById(@PathVariable Long id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colis ID introuvable : " + id));
    }

    // 3. RÉCUPÉRER PAR NUMÉRO DE SUIVI (Pour la recherche client)
    @GetMapping("/numero/{numero}")
    public Colis getColisByNumero(@PathVariable String numero) {
        Colis colis = colisRepository.findByNumeroSuivi(numero);
        if (colis == null) {
            throw new RuntimeException("Colis introuvable avec le numéro : " + numero);
        }
        return colis;
    }

    // 4. LISTER TOUS LES COLIS (Utile pour vérifier ta base)
    @GetMapping("/all")
    public List<Colis> getAllColis() {
        return colisRepository.findAll();
    }
}