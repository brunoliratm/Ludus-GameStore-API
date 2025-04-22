package com.ludus.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.ludus.dtos.requests.GameDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.GameDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.enums.GameGenre;
import com.ludus.enums.GamePlatform;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.ValidationException;
import com.ludus.models.GameModel;
import com.ludus.repositories.GameRepository;
import com.ludus.utils.UtilHelper;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    
    @Mock
    private MessageSource messageSource;
    
    @Mock
    private UtilHelper utilHelper;
    
    @Mock
    private BindingResult bindingResult;
    
    @InjectMocks
    private GameService gameService;
    
    private GameModel testGame;
    private List<GameModel> gameList;
    private GameDtoRequest validGameRequest;
    
    @BeforeEach
    void setUp() {
        testGame = new GameModel();
        testGame.setId(1L);
        testGame.setName("Test Game");
        testGame.setGenre(GameGenre.ACTION);
        testGame.setReleaseYear(2023);
        testGame.setPlatform(GamePlatform.PC);
        testGame.setPrice(BigDecimal.valueOf(59.99));
        
        GameModel game2 = new GameModel();
        game2.setId(2L);
        game2.setName("Another Game");
        game2.setGenre(GameGenre.ADVENTURE);
        game2.setReleaseYear(2022);
        game2.setPlatform(GamePlatform.PLAYSTATION);
        game2.setPrice(BigDecimal.valueOf(49.99));
        
        gameList = new ArrayList<>();
        gameList.add(testGame);
        gameList.add(game2);
        
        validGameRequest = new GameDtoRequest(
            "New Game",
            "ACTION",
            2024,
            "PC",
            59.99f
        );
    }
    
    @Test
    void getAllGames_ValidParameters_ReturnsApiDtoResponse() {
        Page<GameModel> gamePage = new PageImpl<>(gameList);
        InfoDtoResponse mockInfo = new InfoDtoResponse(2L, 1L, null, null);
        
        when(gameRepository.findAll(any(), any(), any(Pageable.class))).thenReturn(gamePage);
        when(utilHelper.buildPageableInfoDto(any(), anyString())).thenReturn(mockInfo);
        ApiDtoResponse<GameDtoResponse> result = gameService.getAllGames(1, null, null);
        
        assertNotNull(result);
        assertEquals(2, result.results().size());
        assertEquals(1L, result.results().get(0).id());
        assertEquals("Test Game", result.results().get(0).name());
        assertEquals("ACTION", result.results().get(0).genre());
        assertEquals(mockInfo, result.info());
        
        verify(gameRepository).findAll(eq(null), eq(null), any(Pageable.class));
        verify(utilHelper).buildPageableInfoDto(eq(gamePage), eq("/games"));
    }
    
    @Test
    void getAllGames_InvalidPage_ThrowsInvalidPageException() {
        assertThrows(InvalidPageException.class, () -> gameService.getAllGames(0, null, null));
        assertThrows(InvalidPageException.class, () -> gameService.getAllGames(-1, null, null));
    }
    
    @Test
    void getAllGames_InvalidGenre_ThrowsNotFoundException() {
        when(messageSource.getMessage(eq("invalid.genre"), any(), any(Locale.class)))
            .thenReturn("Invalid genre");
        
        assertThrows(NotFoundException.class, () -> gameService.getAllGames(1, "INVALID_GENRE", null));
    }
    
    @Test
    void getGame_ExistingId_ReturnsGameDtoResponse() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        
        GameDtoResponse result = gameService.getGame(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Game", result.name());
        assertEquals("ACTION", result.genre());
        assertEquals(2023, result.releaseYear());
        assertEquals("PC", result.platform());
        assertEquals(BigDecimal.valueOf(59.99), result.price());
        
        verify(gameRepository).findById(1L);
    }
    
    @Test
    void getGame_InvalidId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> gameService.getGame(null));
        assertThrows(InvalidIdException.class, () -> gameService.getGame(0L));
        assertThrows(InvalidIdException.class, () -> gameService.getGame(-1L));
    }
    
    @Test
    void getGame_NonExistingId_ThrowsNotFoundException() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("game.not.found"), any(), any(Locale.class)))
            .thenReturn("Game not found");
        
        assertThrows(NotFoundException.class, () -> gameService.getGame(999L));
        verify(gameRepository).findById(999L);
    }
    
    @Test
    void createGame_ValidData_SavesGame() {
        when(bindingResult.hasErrors()).thenReturn(false);
        gameService.createGame(validGameRequest, bindingResult);
        verify(gameRepository).save(any(GameModel.class));
    }
    
    @Test
    void createGame_InvalidGenre_ThrowsValidationException() {

        GameDtoRequest invalidGenreRequest = new GameDtoRequest(
            "New Game",
            "INVALID_GENRE",
            2024,
            "PC",
            59.99f
        );
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(messageSource.getMessage(eq("invalid.genre"), any(), any(Locale.class)))
            .thenReturn("Invalid genre");
        
        assertThrows(ValidationException.class, () -> gameService.createGame(invalidGenreRequest, bindingResult));
    }
    
    @Test
    void createGame_ValidationErrors_ThrowsValidationException() {
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("gameDtoRequest", "name", "Name is required"));
        
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        
        assertThrows(ValidationException.class, () -> gameService.createGame(validGameRequest, bindingResult));
    }
    
    @Test
    void deleteGame_ExistingId_DeletesGame() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        
        gameService.deleteGame(1L);
        
        verify(gameRepository).findById(1L);
        verify(gameRepository).delete(testGame);
    }
    
    @Test
    void deleteGame_InvalidId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> gameService.deleteGame(null));
        assertThrows(InvalidIdException.class, () -> gameService.deleteGame(0L));
        assertThrows(InvalidIdException.class, () -> gameService.deleteGame(-1L));
    }
    
    @Test
    void deleteGame_NonExistingId_ThrowsNotFoundException() {
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("game.not.found"), any(), any(Locale.class)))
            .thenReturn("Game not found");
        
        assertThrows(NotFoundException.class, () -> gameService.deleteGame(999L));
        verify(gameRepository).findById(999L);
    }
}