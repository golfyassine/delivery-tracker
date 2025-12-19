// Fichier : src/app/services/livraison.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LivraisonService {
  // URL vers ta Gateway
  private SERVICE_URL = 'http://localhost:8888/LIVRAISON-SERVICE';

  constructor(private http: HttpClient) {}

  // 1. Pour la liste
  getLivraisons(): Observable<any[]> {
    return this.http.get<any[]>(`${this.SERVICE_URL}/api/livraisons`);
  }

  // 2. CRUCIAL POUR LA CARTE (C'est la méthode qui manquait !)
  getLivraisonSuivi(id: any): Observable<any> {
    return this.http.get<any>(`${this.SERVICE_URL}/suivi/${id}`);
  }

  // 3. Créer une livraison
  creerLivraison(data: any): Observable<any> {
    return this.http.post<any>(`${this.SERVICE_URL}/ajouter`, data);
  }
}
