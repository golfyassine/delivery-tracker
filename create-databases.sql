-- Script SQL pour créer les bases de données PostgreSQL nécessaires
-- Exécutez ce script dans PostgreSQL avant de démarrer les services

-- Base de données pour le service Colis
CREATE DATABASE colis_db;

-- Base de données pour le service Livraison
CREATE DATABASE suivie_db;

-- Base de données pour le service Security
CREATE DATABASE security_db;

-- Vérification des bases créées
SELECT datname FROM pg_database WHERE datname IN ('colis_db', 'suivie_db', 'security_db');


