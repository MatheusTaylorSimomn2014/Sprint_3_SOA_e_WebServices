package com.ford.vinservice.repository;

import com.ford.vinservice.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVin(String vin);

    List<Vehicle> findByCustomerId(Long customerId);

    List<Vehicle> findByWarrantyStatus(String warrantyStatus);

    List<Vehicle> findByNextServiceDateBeforeEqual(java.time.LocalDate date);

    boolean existsByVin(String vin);
}
