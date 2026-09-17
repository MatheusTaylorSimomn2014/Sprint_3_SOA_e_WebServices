package com.ford.loyaltyservice.controller;

import com.ford.loyaltyservice.model.LoyaltyProgram;
import com.ford.loyaltyservice.model.LoyaltyTier;
import com.ford.loyaltyservice.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loyalty Service", description = "API para gerenciamento do programa de fidelidade")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @PostMapping("/register/{customerId}")
    @Operation(summary = "Cadastrar cliente no programa de fidelidade", description = "Registra um novo cliente no programa de fidelidade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Cliente já cadastrado")
    })
    public ResponseEntity<LoyaltyProgram> registerCustomer(
            @Parameter(description = "ID do cliente")
            @PathVariable Long customerId) {
        log.info("POST /api/loyalty/register/{} - Registering customer", customerId);
        
        try {
            LoyaltyProgram program = loyaltyService.registerCustomer(customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(program);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Buscar status de fidelidade", description = "Retorna informações do programa de fidelidade do cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<LoyaltyProgram> getLoyaltyStatus(
            @Parameter(description = "ID do cliente")
            @PathVariable Long customerId) {
        log.info("GET /api/loyalty/{} - Getting loyalty status", customerId);
        
        return loyaltyService.getLoyaltyStatus(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/points")
    @Operation(summary = "Adicionar pontos de fidelidade", description = "Adiciona pontos à conta do cliente após serviço realizado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pontos adicionados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não encontrado")
    })
    public ResponseEntity<LoyaltyProgram> addPoints(
            @Parameter(description = "ID do cliente")
            @RequestParam Long customerId,
            @Parameter(description = "Quantidade de pontos")
            @RequestParam Integer points,
            @Parameter(description = "Valor do serviço em R$")
            @RequestParam Double serviceValue) {
        log.info("POST /api/loyalty/points - Adding {} points for customer {}", points, customerId);
        
        try {
            LoyaltyProgram program = loyaltyService.addPoints(customerId, points, serviceValue);
            return ResponseEntity.ok(program);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tiers")
    @Operation(summary = "Listar níveis de fidelidade", description = "Retorna todos os níveis do programa de fidelidade e seus benefícios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de níveis retornada")
    })
    public ResponseEntity<List<LoyaltyTier>> getAllTiers() {
        log.info("GET /api/loyalty/tiers - Getting all tiers");
        
        List<LoyaltyTier> tiers = loyaltyService.getAllTiers();
        return ResponseEntity.ok(tiers);
    }

    @GetMapping("/benefits/{tierName}")
    @Operation(summary = "Buscar benefícios por nível", description = "Retorna benefícios de um nível específico (BRONZE, SILVER, GOLD, PLATINUM)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benefícios encontrados"),
        @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<LoyaltyTier> getTierBenefits(
            @Parameter(description = "Nome do nível", example = "GOLD")
            @PathVariable String tierName) {
        log.info("GET /api/loyalty/benefits/{} - Getting tier benefits", tierName);
        
        return loyaltyService.getTierBenefits(tierName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
