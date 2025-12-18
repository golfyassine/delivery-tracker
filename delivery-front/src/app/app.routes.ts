import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RechercheComponent } from './components/recherche/recherche'; // Import du nouveau composant

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'recherche', component: RechercheComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
