package com.ludus.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ludus.dto.PurchaseDTO;
import com.ludus.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
@RequestMapping("/api/purchases")
public class PurchaseController {

  @Autowired
  private PurchaseService purchaseService;

  @Operation(summary = "Get All Purchases")
  @GetMapping()
  public ResponseEntity<List<PurchaseDTO>> getPurchases() {
    List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }

  @Operation(summary = "Get a Purchase by ID")
  @GetMapping("/{id}")
  public ResponseEntity<PurchaseDTO> getPurchase(@PathVariable Long id) {
    PurchaseDTO purchase = purchaseService.getPurchase(id);
    return new ResponseEntity<>(purchase, HttpStatus.OK);
  }

  @Operation(summary = "Create a New Purchase")
  @PostMapping()
  public ResponseEntity<Void> createPurchase(@RequestBody PurchaseDTO purchaseDTO) {
    purchaseService.createPurchase(purchaseDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Get a Purchase by user")
  @GetMapping("/user/{id}")
  public ResponseEntity<List<PurchaseDTO>> getPurchasesByUser(@PathVariable Long id) {
    List<PurchaseDTO> purchases = purchaseService.getPurchasesByUser(id);
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }
}
