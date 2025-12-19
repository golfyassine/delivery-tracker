package com.delivery.colis_service.repository;

import com.delivery.colis_service.entities.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "colis")
public interface ColisRepository extends JpaRepository<Colis, Long> {
    Colis findByNumeroSuivi(@Param("numero") String numeroSuivi);
}