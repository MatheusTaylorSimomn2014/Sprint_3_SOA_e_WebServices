package com.ford.vinservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNotFoundException extends RuntimeException {
    
    private String vin;
    private String message;

    public VehicleNotFoundException(String vin) {
        super("Veículo não encontrado com VIN: " + vin);
        this.vin = vin;
    }
}
