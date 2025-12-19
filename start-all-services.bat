@echo off
echo ========================================
echo   DEMARRAGE DES MICROSERVICES
echo ========================================
echo.

echo [1/5] Demarrage du Discovery Service (Eureka) sur le port 8761...
start "Discovery Service" cmd /k "cd /d %~dp0discovery-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [2/5] Demarrage du Security Service sur le port 8080...
start "Security Service" cmd /k "cd /d %~dp0security-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [3/5] Demarrage du Gateway Service sur le port 8888...
start "Gateway Service" cmd /k "cd /d %~dp0gateway-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [4/5] Demarrage du Colis Service sur le port 8081...
start "Colis Service" cmd /k "cd /d %~dp0colis-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [5/5] Demarrage du Livraison Service sur le port 8082...
start "Livraison Service" cmd /k "cd /d %~dp0livraison-service && mvn spring-boot:run"

echo.
echo ========================================
echo   TOUS LES SERVICES SONT EN COURS DE DEMARRAGE
echo ========================================
echo.
echo Veuillez attendre que tous les services soient demarres avant d'utiliser l'application.
echo.
echo Services:
echo   - Discovery Service: http://localhost:8761
echo   - Security Service: http://localhost:8080
echo   - Gateway Service: http://localhost:8888
echo   - Colis Service: http://localhost:8081
echo   - Livraison Service: http://localhost:8082
echo.
echo Appuyez sur une touche pour fermer cette fenetre...
pause >nul
