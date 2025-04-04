package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.models.PurchaseModel;

public interface PurchaseRepository extends JpaRepository<PurchaseModel, Long> {

    Page<PurchaseModel> findByGameAndPaymentMethod(Long gameId, String paymentMethod, Pageable pageable);

    Page<PurchaseModel> findByPaymentMethod(String paymentMethod, Pageable pageable);

    Page<PurchaseModel> findByGameId(Long gameId, Pageable pageable);
}
