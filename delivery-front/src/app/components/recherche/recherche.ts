import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LivraisonService } from '../../services/livraison.service';
import { Livraison } from '../../models/livraison.model';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

@Component({
  selector: 'app-recherche',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recherche.html',
  styleUrl: './recherche.css'
})
export class RechercheComponent implements OnInit {

  public livraison: Livraison | null = null;
  public idRecherche: string = '';
  public isLoading: boolean = false;
  public errorMessage: string | null = null;
  private map: L.Map | null = null;

  constructor(
    private livraisonService: LivraisonService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    // --- CORRECTIF OBLIGATOIRE POUR LES ICÔNES LEAFLET ---
    // Sans ça, les marqueurs ne s'affichent pas dans Angular
    const iconRetinaUrl = 'assets/marker-icon-2x.png';
    const iconUrl = 'assets/marker-icon.png';
    const shadowUrl = 'assets/marker-shadow.png';

    const iconDefault = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    L.Marker.prototype.options.icon = iconDefault;

    // Vérifier si un ID de livraison est passé en paramètre de requête
    // Si oui, lancer la recherche automatiquement
    this.route.queryParams.subscribe(params => {
      const livraisonId = params['id'];
      if (livraisonId) {
        this.idRecherche = livraisonId.toString();
        // Lancer la recherche automatiquement après un court délai
        // pour s'assurer que tout est initialisé
        setTimeout(() => {
          this.rechercher();
        }, 300);
      }
    });
  }

  rechercher() {
    if (!this.idRecherche || !this.idRecherche.trim()) {
      this.errorMessage = "Veuillez entrer un numéro de suivi valide.";
      setTimeout(() => this.errorMessage = null, 5000);
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.livraison = null;

    // Nettoyer la carte existante
    if (this.map) {
      this.map.remove();
      this.map = null;
    }

    this.livraisonService.getLivraisonSuivi(this.idRecherche).subscribe({
      next: (data: Livraison) => {
        this.livraison = data;
        this.isLoading = false;

        // Le setTimeout est CRUCIAL : il attend que le HTML affiche la div "map"
        setTimeout(() => {
          this.chargerCarte();
        }, 100);
      },
      error: (err: any) => {
        console.error('Erreur recherche:', err);
        this.isLoading = false;
        this.livraison = null;
        
        if (err.status === 404) {
          this.errorMessage = "❌ Livraison introuvable. Vérifiez le numéro de suivi.";
        } else if (err.status === 503) {
          this.errorMessage = "⚠️ Service temporairement indisponible. Veuillez réessayer plus tard.";
        } else {
          this.errorMessage = "❌ Erreur serveur. Veuillez réessayer.";
        }
        
        // Masquer l'erreur après 7 secondes
        setTimeout(() => this.errorMessage = null, 7000);
      }
    });
  }

  getProgressPercentage(): number {
    if (!this.livraison) return 0;
    
    switch (this.livraison.statutLivraison) {
      case 'PREPARATION':
        return 33;
      case 'EN_ROUTE':
        return 66;
      case 'LIVRE':
        return 100;
      default:
        return 0;
    }
  }

  getStatusIcon(statut: string | null | undefined): string {
    if (!statut) return 'fa-question-circle';
    
    switch (statut.toUpperCase()) {
      case 'PREPARATION':
        return 'fa-box';
      case 'EN_ROUTE':
        return 'fa-truck';
      case 'LIVRE':
        return 'fa-check-circle';
      case 'INCIDENT':
        return 'fa-exclamation-triangle';
      default:
        return 'fa-info-circle';
    }
  }

  getEstimationText(): string {
    if (!this.livraison) return 'N/A';
    
    switch (this.livraison.statutLivraison) {
      case 'LIVRE':
        return 'Livré avec succès ✓';
      case 'EN_ROUTE':
        return 'En cours de livraison...';
      case 'PREPARATION':
        return 'En préparation';
      case 'INCIDENT':
        return 'Incident signalé ⚠';
      default:
        return 'En cours...';
    }
  }

  chargerCarte() {
    if (this.livraison && this.livraison.colisInfo) {
      const colis = this.livraison.colisInfo;

      // Coordonnées (avec fallback sur Casa si null)
      const lat = colis.latitude || 33.5731;
      const lon = colis.longitude || -7.5898;
      const latDest = colis.latitudeDest;
      const lonDest = colis.longitudeDest;

      // Initialisation de la carte
      this.map = L.map('map').setView([lat, lon], 13);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(this.map);

      // --- TRACÉ DE L'ITINÉRAIRE ---
      if (latDest && lonDest) {
        (L as any).Routing.control({
          waypoints: [
            L.latLng(lat, lon),        // Départ (Livreur)
            L.latLng(latDest, lonDest) // Arrivée (Client)
          ],
          routeWhileDragging: false,
          show: false,               // Masque le texte des instructions (Tournez à droite...)
          addWaypoints: false,
          draggableWaypoints: false,
          fitSelectedRoutes: true,
          lineOptions: {
            styles: [{color: '#2563eb', opacity: 0.8, weight: 6}] // Bleu moderne
          },
          createMarker: function() { return null; } // On cache les marqueurs par défaut du plugin routing
        }).addTo(this.map);

        // Créer des icônes personnalisées pour les marqueurs
        const livreurIcon = L.divIcon({
          className: 'custom-marker-livreur',
          html: '<div style="background: linear-gradient(135deg, #0ea5e9, #0284c7); width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(14, 165, 233, 0.4); border: 3px solid white;"><i class="fas fa-truck" style="color: white; font-size: 18px;"></i></div>',
          iconSize: [40, 40],
          iconAnchor: [20, 40],
          popupAnchor: [0, -40]
        });

        const destinationIcon = L.divIcon({
          className: 'custom-marker-destination',
          html: '<div style="background: linear-gradient(135deg, #10b981, #059669); width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4); border: 3px solid white;"><i class="fas fa-home" style="color: white; font-size: 18px;"></i></div>',
          iconSize: [40, 40],
          iconAnchor: [20, 40],
          popupAnchor: [0, -40]
        });

        // Ajouter les marqueurs avec popups améliorés
        L.marker([lat, lon], { icon: livreurIcon })
          .addTo(this.map)
          .bindPopup(`
            <div style="text-align: center; padding: 5px;">
              <strong style="color: #0ea5e9;">🚚 Livreur</strong><br>
              <small>${colis.adresseActuelle || 'Position actuelle'}</small>
            </div>
          `, {
            className: 'custom-popup'
          })
          .openPopup();

        L.marker([latDest, lonDest], { icon: destinationIcon })
          .addTo(this.map)
          .bindPopup(`
            <div style="text-align: center; padding: 5px;">
              <strong style="color: #10b981;">🏠 Destination</strong><br>
              <small>${colis.adresseDestination || 'Adresse de destination'}</small>
            </div>
          `, {
            className: 'custom-popup'
          });

      } else {
        // Juste le livreur si pas de destination connue
        const livreurIcon = L.divIcon({
          className: 'custom-marker-livreur',
          html: '<div style="background: linear-gradient(135deg, #0ea5e9, #0284c7); width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(14, 165, 233, 0.4); border: 3px solid white;"><i class="fas fa-truck" style="color: white; font-size: 18px;"></i></div>',
          iconSize: [40, 40],
          iconAnchor: [20, 40],
          popupAnchor: [0, -40]
        });

        L.marker([lat, lon], { icon: livreurIcon })
          .addTo(this.map)
          .bindPopup(`
            <div style="text-align: center; padding: 5px;">
              <strong style="color: #0ea5e9;">🚚 Livreur</strong><br>
              <small>${colis.adresseActuelle || 'Position actuelle'}</small>
            </div>
          `, {
            className: 'custom-popup'
          })
          .openPopup();
      }
    }
  }
}
