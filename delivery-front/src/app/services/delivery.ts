import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService { // J'ai renommé la classe en DeliveryService pour être standard

  // L'adresse de ta Gateway
  private apiUrl = 'http://localhost:8888/api/suivi';

  constructor(private http: HttpClient) { }

  getLivraison(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }
}
