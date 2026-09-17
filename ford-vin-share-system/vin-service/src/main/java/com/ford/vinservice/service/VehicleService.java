package com.ford.vinservice.service;

import com.ford.vinservice.dto.VehicleDTO;
import com.ford.vinservice.model.Vehicle;
import com.ford.vinservice.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Register a new vehicle in the system.
     * @param vehicleDTO the vehicle data transfer object
     * @return the created vehicle
     * @throws IllegalArgumentException if VIN already exists
     */
    @Transactional
    public Vehicle registerVehicle(VehicleDTO vehicleDTO) {
        log.info("Registering vehicle with VIN: {}", vehicleDTO.getVin());

        if (vehicleRepository.existsByVin(vehicleDTO.getVin())) {
            throw new IllegalArgumentException("VIN já cadastrado no sistema: " + vehicleDTO.getVin());
        }

        Vehicle vehicle = Vehicle.builder()
                .vin(vehicleDTO.getVin())
                .customerId(vehicleDTO.getCustomerId())
                .make(vehicleDTO.getMake())
                .model(vehicleDTO.getModel())
                .year(vehicleDTO.getYear())
                .mileage(vehicleDTO.getMileage())
                .color(vehicleDTO.getColor())
                .engineType(vehicleDTO.getEngineType())
                .transmission(vehicleDTO.getTransmission())
                .lastServiceDate(vehicleDTO.getLastServiceDate())
                .nextServiceDate(vehicleDTO.getNextServiceDate())
                .warrantyStatus(vehicleDTO.getWarrantyStatus())
                .warrantyExpiryDate(vehicleDTO.getWarrantyExpiryDate())
                .isActive(true)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle registered successfully with ID: {}", savedVehicle.getId());
        
        return savedVehicle;
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleByVin(String vin) {
        log.debug("Searching for vehicle with VIN: {}", vin);
        return vehicleRepository.findByVin(vin);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByCustomerId(Long customerId) {
        log.debug("Searching for vehicles for customer ID: {}", customerId);
        return vehicleRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Vehicle updateVehicle(String vin, VehicleDTO vehicleDTO) {
        log.info("Updating vehicle with VIN: {}", vin);

        Vehicle existingVehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com VIN: " + vin));

        
        if (vehicleDTO.getMileage() != null) {
            existingVehicle.setMileage(vehicleDTO.getMileage());
        }
        if (vehicleDTO.getLastServiceDate() != null) {
            existingVehicle.setLastServiceDate(vehicleDTO.getLastServiceDate());
        }
        if (vehicleDTO.getNextServiceDate() != null) {
            existingVehicle.setNextServiceDate(vehicleDTO.getNextServiceDate());
        }
        if (vehicleDTO.getWarrantyStatus() != null) {
            existingVehicle.setWarrantyStatus(vehicleDTO.getWarrantyStatus());
        }
        if (vehicleDTO.getWarrantyExpiryDate() != null) {
            existingVehicle.setWarrantyExpiryDate(vehicleDTO.getWarrantyExpiryDate());
        }

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        log.info("Vehicle updated successfully with VIN: {}", vin);
        
        return updatedVehicle;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesNeedingService() {
        log.info("Searching for vehicles needing service");
        return vehicleRepository.findByNextServiceDateBeforeEqual(LocalDate.now());
    }

    @Transactional
    public void deleteVehicle(String vin) {
        log.info("Soft deleting vehicle with VIN: {}", vin);
        
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com VIN: " + vin));
        
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
        
        log.info("Vehicle soft deleted successfully with VIN: {}", vin);
    }
}
