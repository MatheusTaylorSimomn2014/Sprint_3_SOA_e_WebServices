package com.ford.loyaltyservice.repository;

import com.ford.loyaltyservice.model.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {

    Optional<LoyaltyTier> findByTierName(String tierName);

    LoyaltyTier findFirstByMinPointsLessThanEqualOrderByMinPointsDesc(Integer points);
}
