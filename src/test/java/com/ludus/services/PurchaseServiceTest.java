package com.ludus.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.ludus.dtos.requests.PurchaseDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.dtos.responses.PurchaseDtoResponse;
import com.ludus.enums.GameGenre;
import com.ludus.enums.GamePlatform;
import com.ludus.enums.PaymentMethod;
import com.ludus.enums.UserRole;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.models.GameModel;
import com.ludus.models.PurchaseModel;
import com.ludus.models.UserModel;
import com.ludus.repositories.GameRepository;
import com.ludus.repositories.PurchaseRepository;
import com.ludus.repositories.UserRepository;
import com.ludus.utils.UtilHelper;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private UtilHelper utilHelper;

    @InjectMocks
    private PurchaseService purchaseService;

    private UserModel testUser;
    private GameModel testGame;
    private PurchaseModel testPurchase;
    private List<PurchaseModel> purchaseList;
    private PurchaseDtoRequest validPurchaseRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USER);
        testUser.setActive(true);

        testGame = new GameModel();
        testGame.setId(1L);
        testGame.setName("Test Game");
        testGame.setGenre(GameGenre.ACTION);
        testGame.setReleaseYear(2023);
        testGame.setPlatform(GamePlatform.PC);
        testGame.setPrice(BigDecimal.valueOf(59.99));

        testPurchase = new PurchaseModel();
        testPurchase.setId(1L);
        testPurchase.setUser(testUser);
        testPurchase.setGame(testGame);
        testPurchase.setPurchaseDate(LocalDate.now());
        testPurchase.setPrice(BigDecimal.valueOf(59.99));
        testPurchase.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        PurchaseModel anotherPurchase = new PurchaseModel();
        anotherPurchase.setId(2L);
        anotherPurchase.setUser(testUser);
        anotherPurchase.setGame(testGame);
        anotherPurchase.setPurchaseDate(LocalDate.now().minusDays(1));
        anotherPurchase.setPrice(BigDecimal.valueOf(59.99));
        anotherPurchase.setPaymentMethod(PaymentMethod.PAYPAL);

        purchaseList = new ArrayList<>();
        purchaseList.add(testPurchase);
        purchaseList.add(anotherPurchase);

        validPurchaseRequest = new PurchaseDtoRequest(1L, 1L, "CREDIT_CARD");
    }

    @Test
    void getAllPurchases_ValidParameters_ReturnsApiDtoResponse() {
        Page<PurchaseModel> purchasePage = new PageImpl<>(purchaseList);
        InfoDtoResponse mockInfo = new InfoDtoResponse(2L, 1L, null, null);
        when(purchaseRepository.findAll(any(), any(), any(Pageable.class))).thenReturn(purchasePage);
        when(utilHelper.buildPageableInfoDto(any(), anyString())).thenReturn(mockInfo);

        ApiDtoResponse<PurchaseDtoResponse> result = purchaseService.getAllPurchases(1, null, null);
        assertNotNull(result);
        assertEquals(2, result.results().size());
        assertEquals(1L, result.results().get(0).id());
        assertEquals("CREDIT_CARD", result.results().get(0).paymentMethod());
        assertEquals(mockInfo, result.info());

        verify(purchaseRepository).findAll(eq(null), eq(null), any(Pageable.class));
        verify(utilHelper).buildPageableInfoDto(eq(purchasePage), eq("/purchases"));
    }

    @Test
    void getAllPurchases_InvalidPage_ThrowsInvalidPageException() {
        assertThrows(InvalidPageException.class, () -> purchaseService.getAllPurchases(0, null, null));
        assertThrows(InvalidPageException.class, () -> purchaseService.getAllPurchases(-1, null, null));
    }

    @Test
    void getAllPurchases_InvalidGameId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> purchaseService.getAllPurchases(1, 0L, null));
        assertThrows(IllegalArgumentException.class, () -> purchaseService.getAllPurchases(1, -1L, null));
    }

    @Test
    void getAllPurchases_InvalidPaymentMethod_ThrowsNotFoundException() {
        when(messageSource.getMessage(eq("purchase.invalid.payment.method"), any(), any(Locale.class)))
            .thenReturn("Invalid payment method");

        assertThrows(NotFoundException.class, () -> purchaseService.getAllPurchases(1, null, "INVALID_METHOD"));
    }

    @Test
    void getPurchase_ExistingId_ReturnsPurchaseDtoResponse() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(testPurchase));
        PurchaseDtoResponse result = purchaseService.getPurchase(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("CREDIT_CARD", result.paymentMethod());
        assertEquals(BigDecimal.valueOf(59.99), result.price());
        assertNotNull(result.game());
        assertEquals(1, result.game().size());
        assertEquals(1L, result.game().get(0).id());
        assertEquals("Test Game", result.game().get(0).name());
        assertNotNull(result.user());
        assertEquals(1, result.user().size());
        assertEquals(1L, result.user().get(0).id());
        assertEquals("Test User", result.user().get(0).name());

        verify(purchaseRepository).findById(1L);
    }

    @Test
    void getPurchase_InvalidId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchase(null));
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchase(0L));
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchase(-1L));
    }

    @Test
    void getPurchase_NonExistingId_ThrowsNotFoundException() {
        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("purchase.not.found"), any(), any(Locale.class)))
            .thenReturn("Purchase not found");

        assertThrows(NotFoundException.class, () -> purchaseService.getPurchase(999L));
        verify(purchaseRepository).findById(999L);
    }

    @Test
    void getPurchasesByUser_ExistingUserId_ReturnsListOfPurchaseDtoResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(purchaseRepository.findAll()).thenReturn(purchaseList);

        List<PurchaseDtoResponse> result = purchaseService.getPurchasesByUser(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("CREDIT_CARD", result.get(0).paymentMethod());

        verify(userRepository).findById(1L);
        verify(purchaseRepository).findAll();
    }

    @Test
    void getPurchasesByUser_InvalidUserId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchasesByUser(null));
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchasesByUser(0L));
        assertThrows(InvalidIdException.class, () -> purchaseService.getPurchasesByUser(-1L));
    }

    @Test
    void getPurchasesByUser_NonExistingUserId_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("user.not.found"), any(), any(Locale.class)))
            .thenReturn("User not found");

        assertThrows(NotFoundException.class, () -> purchaseService.getPurchasesByUser(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void getPurchasesByUser_UserWithNoPurchases_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(purchaseRepository.findAll()).thenReturn(new ArrayList<>());
        when(messageSource.getMessage(eq("purchase.not.found"), any(), any(Locale.class)))
            .thenReturn("Purchase not found");

        assertThrows(NotFoundException.class, () -> purchaseService.getPurchasesByUser(1L));
    }

    @Test
    void createPurchase_ValidData_SavesPurchase() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        purchaseService.createPurchase(validPurchaseRequest);

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(purchaseRepository).save(any(PurchaseModel.class));
    }

    @Test
    void createPurchase_InvalidUserId_ThrowsInvalidIdException() {
        PurchaseDtoRequest invalidUserRequest = new PurchaseDtoRequest(null, 1L, "CREDIT_CARD");
        PurchaseDtoRequest negativeUserRequest = new PurchaseDtoRequest(-1L, 1L, "CREDIT_CARD");

        assertThrows(InvalidIdException.class, () -> purchaseService.createPurchase(invalidUserRequest));
        assertThrows(InvalidIdException.class, () -> purchaseService.createPurchase(negativeUserRequest));
    }

    @Test
    void createPurchase_InvalidGameId_ThrowsInvalidIdException() {
        PurchaseDtoRequest invalidGameRequest = new PurchaseDtoRequest(1L, null, "CREDIT_CARD");
        PurchaseDtoRequest negativeGameRequest = new PurchaseDtoRequest(1L, -1L, "CREDIT_CARD");

        assertThrows(InvalidIdException.class, () -> purchaseService.createPurchase(invalidGameRequest));
        assertThrows(InvalidIdException.class, () -> purchaseService.createPurchase(negativeGameRequest));
    }

    @Test
    void createPurchase_NonExistingUser_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("purchase.user.not.found"), any(), any(Locale.class)))
            .thenReturn("User not found for purchase");

        PurchaseDtoRequest nonExistingUserRequest = new PurchaseDtoRequest(999L, 1L, "CREDIT_CARD");

        assertThrows(NotFoundException.class, () -> purchaseService.createPurchase(nonExistingUserRequest));
        verify(userRepository).findById(999L);
    }

    @Test
    void createPurchase_NonExistingGame_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("purchase.game.not.found"), any(), any(Locale.class)))
            .thenReturn("Game not found for purchase");

        PurchaseDtoRequest nonExistingGameRequest = new PurchaseDtoRequest(1L, 999L, "CREDIT_CARD");

        assertThrows(NotFoundException.class, () -> purchaseService.createPurchase(nonExistingGameRequest));
        verify(userRepository).findById(1L);
        verify(gameRepository).findById(999L);
    }

    @Test
    void createPurchase_InvalidPaymentMethod_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(messageSource.getMessage(eq("purchase.invalid.payment.method"), any(), any(Locale.class)))
            .thenReturn("Invalid payment method");

        PurchaseDtoRequest invalidPaymentMethodRequest = new PurchaseDtoRequest(1L, 1L, "INVALID_METHOD");

        assertThrows(NotFoundException.class, () -> purchaseService.createPurchase(invalidPaymentMethodRequest));
        verify(userRepository).findById(1L);
        verify(gameRepository).findById(1L);
    }
}