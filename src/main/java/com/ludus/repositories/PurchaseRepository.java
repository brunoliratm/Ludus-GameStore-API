package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ludus.models.PurchaseModel;
import com.ludus.enums.PaymentMethod;

public interface PurchaseRepository extends JpaRepository<PurchaseModel, Long> {

    @Query("SELECT p FROM PurchaseModel p WHERE p.game.id = :gameId AND p.paymentMethod = :paymentMethod")
    Page<PurchaseModel> findByGameIdAndPaymentMethod(@Param("gameId") Long gameId, @Param("paymentMethod") PaymentMethod paymentMethod, Pageable pageable);
    
    Page<PurchaseModel> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    Page<PurchaseModel> findByGameId(Long gameId, Pageable pageable);
}
