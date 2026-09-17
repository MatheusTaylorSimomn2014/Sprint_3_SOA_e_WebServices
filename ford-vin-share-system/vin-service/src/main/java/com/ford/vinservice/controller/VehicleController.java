package com.ford.vinservice.controller;

import com.ford.vinservice.dto.VehicleDTO;
import com.ford.vinservice.model.Vehicle;
import com.ford.vinservice.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/vin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "VIN Service", description = "API para gerenciamento de veículos via VIN")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Cadastrar novo veículo", description = "Registra um novo veículo no sistema usando o VIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou VIN já cadastrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER')")
    public ResponseEntity<Vehicle> registerVehicle(
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        log.info("POST /api/vin - Registering vehicle: {}", vehicleDTO.getVin());
        
        Vehicle createdVehicle = vehicleService.registerVehicle(vehicleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping("/{vin}")
    @Operation(summary = "Buscar veículo por VIN", description = "Retorna informações do veículo identificado pelo VIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Veículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<Vehicle> getVehicleByVin(
            @Parameter(description = "Vehicle Identification Number (17 caracteres)", example = "1FADP3K29HL123456")
            @PathVariable String vin) {
        log.info("GET /api/vin/{} - Searching vehicle", vin);
        
        return vehicleService.getVehicleByVin(vin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Buscar veículos por cliente", description = "Retorna todos os veículos de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Veículos encontrados"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - cliente não pode acessar dados de outros clientes")
    })
    public ResponseEntity<List<Vehicle>> getVehiclesByCustomerId(
            @Parameter(description = "ID do cliente")
            @PathVariable Long customerId) {
        log.info("GET /api/vin/customer/{} - Searching vehicles", customerId);
        
        // Security fix: Validate that user can only access their own data
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                boolean hasAdminOrDealerRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                   a.getAuthority().equals("ROLE_DEALER"));

                if (!hasAdminOrDealerRole) {
                    Long authenticatedCustomerId = (Long) 
                        ((org.springframework.security.core.userdetails.UserDetails) principal)
                            .getClass().getMethod("getCustomerId").invoke(principal);
                    
                    if (!customerId.equals(authenticatedCustomerId)) {
                        log.warn("User {} attempted to access data for customer {}", 
                            authentication.getName(), customerId);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }
        }
        
        List<Vehicle> vehicles = vehicleService.getVehiclesByCustomerId(customerId);
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{vin}")
    @Operation(summary = "Atualizar veículo", description = "Atualiza informações de um veículo existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER')")
    public ResponseEntity<Vehicle> updateVehicle(
            @Parameter(description = "Vehicle Identification Number")
            @PathVariable String vin,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        log.info("PUT /api/vin/{} - Updating vehicle", vin);
        
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(vin, vehicleDTO);
            return ResponseEntity.ok(updatedVehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/service/pending")
    @Operation(summary = "Veículos precisando de serviço", description = "Retorna veículos que estão com revisão vencida ou próxima")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de veículos retornada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER')")
    public ResponseEntity<List<Vehicle>> getVehiclesNeedingService() {
        log.info("GET /api/vin/service/pending - Searching vehicles needing service");
        
        List<Vehicle> vehicles = vehicleService.getVehiclesNeedingService();
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/{vin}")
    @Operation(summary = "Excluir veículo", description = "Remove logicamente um veículo do sistema (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Veículo excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "Vehicle Identification Number")
            @PathVariable String vin) {
        log.info("DELETE /api/vin/{} - Deleting vehicle", vin);
        
        try {
            vehicleService.deleteVehicle(vin);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
