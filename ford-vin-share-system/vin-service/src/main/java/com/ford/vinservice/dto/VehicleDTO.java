package com.ford.vinservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    @NotBlank(message = "VIN é obrigatório")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN deve ter exatamente 17 caracteres alfanuméricos válidos (sem I, O, Q)")
    private String vin;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long customerId;

    private String make;

    private String model;

    private Integer year;

    private Integer mileage;

    private String color;

    private String engineType;

    private String transmission;

    private LocalDate lastServiceDate;

    private LocalDate nextServiceDate;

    private String warrantyStatus;

    private LocalDate warrantyExpiryDate;
}
