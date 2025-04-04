package com.ludus.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ludus.dtos.requests.PurchaseDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.dtos.responses.PurchaseDtoResponse;
import com.ludus.enums.PaymentMethod;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.models.GameModel;
import com.ludus.models.PurchaseModel;
import com.ludus.models.UserModel;
import com.ludus.repositories.GameRepository;
import com.ludus.repositories.PurchaseRepository;
import com.ludus.repositories.UserRepository;
import com.ludus.utils.UtilHelper;

@Service
public class PurchaseService {
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UtilHelper utilHelper;

    public ApiDtoResponse<PurchaseDtoResponse> getAllPurchases(
        int page,
        Long gameId,
        String paymentMethod
    ) {
        if (page < 1) {
            throw new InvalidPageException("Page number must be greater than 0");
        }

        if (gameId != null && gameId < 1) {
            throw new IllegalArgumentException("Game ID must be greater than 0");
        }

        int pageIndex = page - 1;
        Pageable pageable = PageRequest.of(pageIndex, 10);

        Page<PurchaseModel> purchasePage;

        if (paymentMethod != null) {
            try {
                PaymentMethod.valueOf(paymentMethod.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new NotFoundException(
                    messageSource.getMessage("purchase.invalid.payment.method", null, Locale.getDefault())
                );
            }
        }

        if (gameId != null && paymentMethod != null) {
            purchasePage = purchaseRepository.findByGameAndPaymentMethod(gameId, paymentMethod, pageable);
        } else if (gameId != null) {
            purchasePage = purchaseRepository.findByGameId(gameId, pageable);
        } else if (paymentMethod != null) {
            purchasePage = purchaseRepository.findByPaymentMethod(paymentMethod, pageable);
        } else {
            purchasePage = purchaseRepository.findAll(pageable);
        }

        List<PurchaseDtoResponse> purchaseDTOs = purchasePage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        InfoDtoResponse info = utilHelper.buildPageableInfoDto(purchasePage, "/purchases");
        return new ApiDtoResponse<>(info, purchaseDTOs);
    }

    public PurchaseDtoResponse getPurchase(Long id) {
        if (id == null || id < 1) {
            throw new InvalidIdException();
        }
        
        PurchaseModel purchaseModel = purchaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("purchase.not.found", new Object[]{id}, Locale.getDefault())));
        return convertToDTO(purchaseModel);
    }
    
    public List<PurchaseDtoResponse> getPurchasesByUser(Long userId) {
        if (userId == null || userId < 1) {
            throw new InvalidIdException();
        }
        
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{userId}, Locale.getDefault())));
        
        try {
            return purchaseRepository.findAll().stream()
                    .filter(purchase -> purchase.getUser().getId().equals(userId))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("retrieval.error", null, Locale.getDefault()));
        }
    }

    public void createPurchase(PurchaseDtoRequest purchaseDTO) {
        try {
            PurchaseModel purchaseModel = new PurchaseModel();
            
            UserModel user = userRepository.findById(purchaseDTO.userId())
                    .orElseThrow(() -> new NotFoundException(
                            messageSource.getMessage("purchase.user.not.found", null, Locale.getDefault())));
            purchaseModel.setUser(user);
            
            GameModel game = gameRepository.findById(purchaseDTO.gameId())
                    .orElseThrow(() -> new NotFoundException(
                            messageSource.getMessage("purchase.game.not.found", null, Locale.getDefault())));
            purchaseModel.setGame(game);
            
            purchaseModel.setPurchaseDate(purchaseDTO.purchaseDate() != null ? 
                    purchaseDTO.purchaseDate() : LocalDate.now());
            purchaseModel.setPrice(game.getPrice());
            
            try {
                PaymentMethod paymentMethod = PaymentMethod.valueOf(purchaseDTO.paymentMethod().toUpperCase().trim());
                purchaseModel.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new NotFoundException(
                        messageSource.getMessage("purchase.invalid.payment.method", null, Locale.getDefault()));
            }
            
            purchaseRepository.save(purchaseModel);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RetrievalException(
                    messageSource.getMessage("purchase.creation.error", null, Locale.getDefault()));
        }
    }
    
    private PurchaseDtoResponse convertToDTO(PurchaseModel purchaseModel) {
        return new PurchaseDtoResponse(
                purchaseModel.getId(),
                purchaseModel.getUser().getId(),
                purchaseModel.getGame().getId(),
                purchaseModel.getPurchaseDate(),
                purchaseModel.getPrice(),
                purchaseModel.getPaymentMethod().toString()
        );
    }
}
