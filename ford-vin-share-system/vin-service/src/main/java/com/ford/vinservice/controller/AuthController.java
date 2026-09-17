package com.ford.vinservice.controller;

import com.ford.vinservice.dto.AuthRequest;
import com.ford.vinservice.dto.AuthResponse;
import com.ford.vinservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API para autenticação e geração de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT (15 min) + refresh token (7 dias)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        log.info("POST /api/auth/login - User: {}", authRequest.getUsername());
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Atualizar token JWT", description = "Gera um novo access token usando um refresh token válido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        log.info("POST /api/auth/refresh - Refreshing token");
        return ResponseEntity.ok(null); 
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Registra um novo usuário no sistema (apenas para demonstração)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(defaultValue = "CUSTOMER") String role,
            @RequestParam(required = false) Long customerId) {

        log.info("POST /api/auth/register - Registering user: {} with role: {}", username, role);
        AuthResponse response = authService.registerUser(username, password, role, customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter informações do usuário autenticado", description = "Retorna informações do usuário atualmente autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações do usuário retornadas"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<String> getCurrentUser() {
        org.springframework.security.core.context.SecurityContext context =
            org.springframework.security.core.context.SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        return ResponseEntity.ok("Logged in as: " + username);
    }
}
