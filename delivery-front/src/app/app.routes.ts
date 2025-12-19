import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RechercheComponent } from './components/recherche/recherche';
import {DashboardComponent} from './components/dashboard/dashboard'; // Import du nouveau composant

// import { AuthGuard } from './guards/auth.guard'; // À décommenter quand tu créeras la sécurité

export const routes: Routes = [
  // 1. Route par défaut : Redirection vers Login (ou Recherche si c'est public)
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // 2. Page de Login
  {
    path: 'login',
    component: LoginComponent,
    title: 'Connexion - Suivi Livraison' // Change le titre de l'onglet du navigateur
  },

  // 3. Page de Recherche (Publique - pour que les clients suivent leur colis)
  {
    path: 'recherche',
    component: RechercheComponent,
    title: 'Suivre mon colis'
  },

  // 4. Dashboard (Privé - nécessite une connexion plus tard)
  {
    path: 'dashboard',
    component: DashboardComponent,
    title: 'Tableau de bord Admin',
    // canActivate: [AuthGuard] // C'est ici qu'on bloquera l'accès aux non-connectés
  },

  // 5. Gestion des liens morts (404) : Redirige tout le reste vers Login
  { path: '**', redirectTo: 'login' }
];
