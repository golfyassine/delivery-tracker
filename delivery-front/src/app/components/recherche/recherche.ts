import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryService } from '../../services/delivery';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

@Component({
  selector: 'app-recherche',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recherche.html',
  styleUrl: './recherche.css'
})
export class RechercheComponent implements OnInit { // <-- Nom de classe corrigé

  public livraison: any = null;
  public idRecherche: any = '';
  private map: any;

  constructor(private deliveryService: DeliveryService) {}

  ngOnInit() {}

  rechercher() {
    if (!this.idRecherche) {
      alert("Veuillez entrer un numéro de livraison !");
      return;
    }

    this.deliveryService.getLivraison(this.idRecherche).subscribe({
      next: (data) => {
        this.livraison = data;
        if (this.map) {
          this.map.remove();
          this.map = null;
        }
        setTimeout(() => {
          this.chargerCarte();
        }, 100);
      },
      error: (err) => {
        alert("❌ Livraison introuvable ou erreur serveur.");
        this.livraison = null;
      }
    });
  }

  chargerCarte() {
    if (this.livraison && this.livraison.colisInfo) {
      const colis = this.livraison.colisInfo;
      const lat = colis.latitude;
      const lon = colis.longitude;
      const latDest = colis.latitudeDest;
      const lonDest = colis.longitudeDest;

      this.map = L.map('map').setView([lat, lon], 12);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);

      if (latDest && lonDest) {
        (L as any).Routing.control({
          waypoints: [L.latLng(lat, lon), L.latLng(latDest, lonDest)],
          routeWhileDragging: false,
          show: false,
          addWaypoints: false,
          draggableWaypoints: false,
          fitSelectedRoutes: true,
          lineOptions: { styles: [{color: '#007bff', opacity: 0.8, weight: 6}] },
          createMarker: function() { return null; }
        }).addTo(this.map);

        L.marker([lat, lon]).addTo(this.map).bindPopup("🚚 <b>Livreur</b>").openPopup();
        L.marker([latDest, lonDest]).addTo(this.map).bindPopup("🏠 <b>Destination</b>");
      } else {
        L.marker([lat, lon]).addTo(this.map).bindPopup("🚚 <b>Livreur</b>").openPopup();
      }
    }
  }
}
