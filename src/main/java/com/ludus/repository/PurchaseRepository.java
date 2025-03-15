package com.ludus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.models.PurchaseModel;

public interface PurchaseRepository extends JpaRepository<PurchaseModel, Long> {
  

}
