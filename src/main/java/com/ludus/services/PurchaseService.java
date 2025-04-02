package com.ludus.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.ludus.dto.PurchaseDTO;
import com.ludus.enums.PaymentMethod;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.models.GameModel;
import com.ludus.models.PurchaseModel;
import com.ludus.models.UserModel;
import com.ludus.repository.GameRepository;
import com.ludus.repository.PurchaseRepository;
import com.ludus.repository.UserRepository;

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

    public List<PurchaseDTO> getAllPurchases() {
        try {
            List<PurchaseModel> purchaseModels = purchaseRepository.findAll();
            return purchaseModels.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RetrievalException(messageSource.getMessage("retrieval.error", null, Locale.getDefault()));
        }
    }

    public PurchaseDTO getPurchase(Long id) {
        if (id == null || id < 1) {
            throw new InvalidIdException();
        }
        
        PurchaseModel purchaseModel = purchaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("purchase.not.found", new Object[]{id}, Locale.getDefault())));
        return convertToDTO(purchaseModel);
    }
    
    public List<PurchaseDTO> getPurchasesByUser(Long userId) {
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

    public void createPurchase(PurchaseDTO purchaseDTO) {
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
                    purchaseDTO.purchaseDate() : LocalDateTime.now());
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
    
    private PurchaseDTO convertToDTO(PurchaseModel purchaseModel) {
        return new PurchaseDTO(
                purchaseModel.getUser().getId(),
                purchaseModel.getGame().getId(),
                purchaseModel.getPurchaseDate(),
                purchaseModel.getPrice(),
                purchaseModel.getPaymentMethod().toString()
        );
    }
}
