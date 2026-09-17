package com.ford.loyaltyservice.service;

import com.ford.loyaltyservice.model.LoyaltyProgram;
import com.ford.loyaltyservice.model.LoyaltyTier;
import com.ford.loyaltyservice.repository.LoyaltyProgramRepository;
import com.ford.loyaltyservice.repository.LoyaltyTierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final LoyaltyProgramRepository loyaltyProgramRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;

    @Transactional
    public LoyaltyProgram registerCustomer(Long customerId) {
        log.info("Registering customer {} in loyalty program", customerId);

        if (loyaltyProgramRepository.existsByCustomerId(customerId)) {
            throw new IllegalArgumentException("Cliente já cadastrado no programa de fidelidade");
        }

        
        LoyaltyTier bronzeTier = loyaltyTierRepository.findByTierName("BRONZE")
                .orElseThrow(() -> new RuntimeException("Nível BRONZE não configurado"));

        LoyaltyProgram program = LoyaltyProgram.builder()
                .customerId(customerId)
                .tier(bronzeTier)
                .points(0)
                .totalServices(0)
                .totalSpent(0.0)
                .isActive(true)
                .build();

        LoyaltyProgram saved = loyaltyProgramRepository.save(program);
        log.info("Customer {} registered successfully with tier {}", customerId, bronzeTier.getTierName());
        
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<LoyaltyProgram> getLoyaltyStatus(Long customerId) {
        log.debug("Getting loyalty status for customer {}", customerId);
        return loyaltyProgramRepository.findByCustomerId(customerId);
    }

    @Transactional
    public LoyaltyProgram addPoints(Long customerId, Integer points, Double serviceValue) {
        log.info("Adding {} points for customer {}", points, customerId);

        LoyaltyProgram program = loyaltyProgramRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado no programa de fidelidade"));

        
        program.setPoints(program.getPoints() + points);
        
        
        program.setTotalServices(program.getTotalServices() + 1);
        program.setTotalSpent(program.getTotalSpent() + serviceValue);

        
        LoyaltyTier newTier = loyaltyTierRepository.findFirstByMinPointsLessThanEqualOrderByMinPointsDesc(
                program.getPoints());
        
        if (newTier != null && !newTier.equals(program.getTier())) {
            log.info("Customer {} upgraded from {} to {}", customerId, 
                    program.getTier().getTierName(), newTier.getTierName());
            program.setTier(newTier);
        }

        LoyaltyProgram updated = loyaltyProgramRepository.save(program);
        log.info("Added {} points to customer {}. Total: {}", points, customerId, updated.getPoints());
        
        return updated;
    }

    @Transactional(readOnly = true)
    public List<LoyaltyTier> getAllTiers() {
        log.info("Getting all loyalty tiers");
        return loyaltyTierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<LoyaltyTier> getTierBenefits(String tierName) {
        log.debug("Getting benefits for tier {}", tierName);
        return loyaltyTierRepository.findByTierName(tierName);
    }
}
