package com.ford.loyaltyservice.repository;

import com.ford.loyaltyservice.model.LoyaltyProgram;
import com.ford.loyaltyservice.model.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {

    Optional<LoyaltyProgram> findByCustomerId(Long customerId);

    List<LoyaltyProgram> findByTier(LoyaltyTier tier);

    boolean existsByCustomerId(Long customerId);
}
