package com.ludus.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody; 
import com.ludus.dtos.requests.PurchaseDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.PurchaseDtoResponse;
import com.ludus.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/${api.version}/purchases")
@Tag(name = "Purchases", description = "Endpoints for purchase management")
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
      this.purchaseService = purchaseService;
  }

  @Operation(
    summary = "Get All Purchases", 
    description = "Retrieves a paginated list of purchases with optional filtering by game ID and payment method",
    responses = {
        @ApiResponse(responseCode = "200", description = "Purchases found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid Page", value = "{\"message\": \"Page number must be greater than 0\"}"),
                @ExampleObject(name = "Invalid Page Format", value = "{\"message\": \"Invalid Page format: must be a number\"}"),
                @ExampleObject(name = "Invalid Game ID", value = "{\"message\": \"Game ID must be greater than 0\"}"),
                @ExampleObject(name = "Invalid Payment Method", value = "{\"message\": \"Invalid payment method. Valid options are: CREDIT_CARD, DEBIT_CARD, PIX, PAYPAL, BOLETO, OTHER\"}")
            })
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
            )
        ),
        @ApiResponse(responseCode = "403", description = "Forbidden access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
            )
        )
    }
  )
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

  @Operation(
    summary = "Get a Purchase by ID", 
    description = "Retrieves a specific purchase by its ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Purchase found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID provided", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                @ExampleObject(name = "Invalid Format", value = "{\"message\": \"Invalid ID format: must be a number\", \"details\": \"The value 'abc' is not valid for parameter 'id'\"}")
            })
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
            )
        ),
        @ApiResponse(responseCode = "403", description = "Forbidden access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Purchase not found", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Purchase not found with id: 123\"}")
            )
        )
    }
  )
  @GetMapping("/{id}")
  public ResponseEntity<PurchaseDtoResponse> getPurchase(@PathVariable Long id) {
    PurchaseDtoResponse purchase = purchaseService.getPurchase(id);
    return new ResponseEntity<>(purchase, HttpStatus.OK);
  }

  @Operation(
    summary = "Create a New Purchase", 
    description = "Creates a new purchase with the provided details",
    responses = {
        @ApiResponse(responseCode = "201", description = "Purchase created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid purchase data provided", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid User ID", value = "{\"message\": \"User ID must not be null or less than 1\"}"),
                @ExampleObject(name = "Invalid Game ID", value = "{\"message\": \"Game ID must not be null or less than 1\"}"),
                @ExampleObject(name = "Invalid Payment Method", value = "{\"message\": \"Invalid payment method. Valid options are: CREDIT_CARD, DEBIT_CARD, PIX, PAYPAL, BOLETO, OTHER\"}")
            })
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
            )
        ),
        @ApiResponse(responseCode = "403", description = "Forbidden access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Resource not found", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "User Not Found", value = "{\"message\": \"User not found for purchase\"}"),
                @ExampleObject(name = "Game Not Found", value = "{\"message\": \"Game not found for purchase\"}")
            })
        ),
        @ApiResponse(responseCode = "500", description = "Error creating purchase", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Error creating purchase\"}")
            )
        )
    }
  )
  @PostMapping()
  public ResponseEntity<Void> createPurchase(@RequestBody PurchaseDtoRequest purchaseDTO) {
    purchaseService.createPurchase(purchaseDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(
    summary = "Get Purchases by User ID", 
    description = "Retrieves all purchases made by a specific user",
    responses = {
        @ApiResponse(responseCode = "200", description = "Purchases found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID provided", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                @ExampleObject(name = "Invalid Format", value = "{\"message\": \"Invalid ID format: must be a number\", \"details\": \"The value 'abc' is not valid for parameter 'id'\"}")
            })
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
            )
        ),
        @ApiResponse(responseCode = "403", description = "Forbidden access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"You don't have permission to access this resource\"}")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Resources not found", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "User Not Found", value = "{\"message\": \"User not found with id: 123\"}"),
                @ExampleObject(name = "No Purchases", value = "{\"message\": \"Purchase not found with id: usuário 123\"}")
            })
        ),
        @ApiResponse(responseCode = "500", description = "Error retrieving data", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Error retrieving data\"}")
            )
        )
    }
  )
  @GetMapping("/user/{id}")
  public ResponseEntity<List<PurchaseDtoResponse>> getPurchasesByUser(@PathVariable Long id) {
    List<PurchaseDtoResponse> purchases = purchaseService.getPurchasesByUser(id);
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }
}
