import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // Ajout de withInterceptors
import { authInterceptor } from './interceptors/auth-interceptor'; // On ajoute le tiret ici !
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // On active le HttpClient ET on lui dit d'utiliser notre intercepteur de Token
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
