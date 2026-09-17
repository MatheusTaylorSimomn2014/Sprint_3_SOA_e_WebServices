package com.ford.vinservice.service;

import com.ford.vinservice.dto.AuthRequest;
import com.ford.vinservice.dto.AuthResponse;
import com.ford.vinservice.security.CustomUserDetails;
import com.ford.vinservice.security.CustomUserDetailsService;
import com.ford.vinservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:900000}")
    private Long jwtExpiration;

    public AuthResponse authenticate(AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.getUsername());

        try {
            // Authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

            String token = jwtUtil.generateToken(
                userDetails,
                customUserDetails.getRole(),
                customUserDetails.getCustomerId()
            );

            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            log.info("User {} authenticated successfully", authRequest.getUsername());

            return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(customUserDetails.getUsername())
                .role(customUserDetails.getRole())
                .customerId(customUserDetails.getCustomerId())
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .build();

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", authRequest.getUsername());
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }
    }

    public AuthResponse registerUser(String username, String password, String role, Long customerId) {
        log.info("Registering new user: {} with role: {}", username, role);

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
        }

        try {
            userDetailsService.getUserByUsername(username);
            throw new IllegalArgumentException("Usuário já existe");
        } catch (Exception e) {

        }

        String encodedPassword = passwordEncoder.encode(password);

        CustomUserDetails newUser = new CustomUserDetails(
            username,
            encodedPassword,
            role,
            customerId
        );

        userDetailsService.addUser(newUser);

        return authenticate(new AuthRequest(username, password));
    }
}
