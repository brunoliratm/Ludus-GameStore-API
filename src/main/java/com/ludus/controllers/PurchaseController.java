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
import org.springframework.web.bind.annotation.RequestParam;
import com.ludus.dtos.requests.PurchaseDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.PurchaseDtoResponse;
import com.ludus.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
@RequestMapping("/api/${api.version}/purchases")
public class PurchaseController {

  @Autowired
  private PurchaseService purchaseService;

  @Operation(summary = "Get All Purchases")
  @GetMapping()
  public ResponseEntity<ApiDtoResponse<PurchaseDtoResponse>> getPurchases(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(required = false) Long gameId,
      @RequestParam(required = false) String paymentMethod
  ) {
    ApiDtoResponse<PurchaseDtoResponse> purchases = purchaseService.getAllPurchases(
      page, gameId, paymentMethod
    );
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }

  @Operation(summary = "Get a Purchase by ID")
  @GetMapping("/{id}")
  public ResponseEntity<PurchaseDtoResponse> getPurchase(@PathVariable Long id) {
    PurchaseDtoResponse purchase = purchaseService.getPurchase(id);
    return new ResponseEntity<>(purchase, HttpStatus.OK);
  }

  @Operation(summary = "Create a New Purchase")
  @PostMapping()
  public ResponseEntity<Void> createPurchase(@RequestBody PurchaseDtoRequest purchaseDTO) {
    purchaseService.createPurchase(purchaseDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Get a Purchase by user")
  @GetMapping("/user/{id}")
  public ResponseEntity<List<PurchaseDtoResponse>> getPurchasesByUser(@PathVariable Long id) {
    List<PurchaseDtoResponse> purchases = purchaseService.getPurchasesByUser(id);
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }
}
