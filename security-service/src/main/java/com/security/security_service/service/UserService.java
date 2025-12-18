package com.security.security_service.service;

import com.security.security_service.entities.AppUser;
import com.security.security_service.entities.Role;
import com.security.security_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(String username, String password, Role role) {
        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // On crypte le mot de passe ici !
                .role(role)
                .build();
        return userRepository.save(user);
    }
}