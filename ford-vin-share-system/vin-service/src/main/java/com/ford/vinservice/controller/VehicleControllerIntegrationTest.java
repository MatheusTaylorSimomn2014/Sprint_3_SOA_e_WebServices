package com.ford.vinservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.vinservice.dto.AuthRequest;
import com.ford.vinservice.dto.VehicleDTO;
import com.ford.vinservice.model.Vehicle;
import com.ford.vinservice.repository.VehicleRepository;
import com.ford.vinservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        vehicleRepository.deleteAll();

        AuthRequest authRequest = new AuthRequest("admin", "password");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        authToken = "Bearer test-token";
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateVehicle_Success() throws Exception {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVin("1FADP3K29HL123456");
        vehicleDTO.setCustomerId(1L);
        vehicleDTO.setMake("Ford");
        vehicleDTO.setModel("Fiesta");
        vehicleDTO.setYear(2020);
        vehicleDTO.setMileage(15000);
        vehicleDTO.setColor("Prata");
        vehicleDTO.setLastServiceDate(LocalDate.now().minusMonths(6));
        vehicleDTO.setNextServiceDate(LocalDate.now().plusMonths(6));

        mockMvc.perform(post("/api/vin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.vin").value("1FADP3K29HL123456"))
            .andExpect(jsonPath("$.make").value("Ford"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetVehicleByVin_Success() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("1FADP3K29HL654321");
        vehicle.setCustomerId(1L);
        vehicle.setMake("Ford");
        vehicle.setModel("Ka");
        vehicle.setYear(2021);
        vehicleRepository.save(vehicle);

        mockMvc.perform(get("/api/vin/1FADP3K29HL654321"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.vin").value("1FADP3K29HL654321"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetVehicleByVin_NotFound() throws Exception {
        mockMvc.perform(get("/api/vin/INVALIDVIN1234567"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DEALER")
    void testUpdateVehicle_Success() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("1FADP3K29HL999999");
        vehicle.setCustomerId(1L);
        vehicle.setMake("Ford");
        vehicle.setModel("Ranger");
        vehicle.setYear(2019);
        vehicleRepository.save(vehicle);

        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setMileage(50000);
        updateDTO.setLastServiceDate(LocalDate.now());

        mockMvc.perform(put("/api/vin/1FADP3K29HL999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mileage").value(50000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteVehicle_Success() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("1FADP3K29HL777777");
        vehicle.setCustomerId(1L);
        vehicle.setMake("Ford");
        vehicle.setModel("Focus");
        vehicle.setYear(2018);
        vehicleRepository.save(vehicle);

        mockMvc.perform(delete("/api/vin/1FADP3K29HL777777"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/vin/1FADP3K29HL777777"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void testGetVehicleWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/vin/1FADP3K29HL123456"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateVehicleAsCustomer_Forbidden() throws Exception {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setVin("1FADP3K29HL888888");
        vehicleDTO.setCustomerId(1L);

        mockMvc.perform(post("/api/vin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDTO)))
            .andExpect(status().isForbidden());
    }
}
