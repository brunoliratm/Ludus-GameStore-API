package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ludus.enums.PaymentMethod;
import com.ludus.models.PurchaseModel;

public interface PurchaseRepository extends JpaRepository<PurchaseModel, Long> {

    @Query("""
    SELECT p FROM PurchaseModel p
    WHERE (:gameId IS NULL OR p.game.id = :gameId)
    AND (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod)
    """)
Page<PurchaseModel> findAll(@Param("gameId")Long gameId, 
                                               @Param("paymentMethod")PaymentMethod paymentMethod, 
                                               Pageable pageable);
}
