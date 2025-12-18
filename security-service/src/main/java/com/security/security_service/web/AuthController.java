package com.security.security_service.web;

import com.security.security_service.dto.AuthRequest;
import com.security.security_service.dto.AuthResponse;
import com.security.security_service.entities.AppUser;
import com.security.security_service.entities.Role;
import com.security.security_service.repository.UserRepository;
import com.security.security_service.security.JwtUtils;
import com.security.security_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // 1. ENDPOINT POUR S'INSCRIRE (Créer un compte)
    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AuthRequest request) {
        // Par défaut, on crée un rôle USER
        return ResponseEntity.ok(userService.registerUser(request.getUsername(), request.getPassword(), Role.USER));
    }

    // 2. ENDPOINT POUR SE CONNECTER (Obtenir le Token)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // On cherche l'utilisateur en base
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // On vérifie si le mot de passe correspond (en comparant le clair avec le haché)
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Si OK, on génère le token
            String token = jwtUtils.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
        } else {
            return ResponseEntity.status(401).body("Mot de passe incorrect");
        }
    }
}