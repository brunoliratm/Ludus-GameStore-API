package com.ludus.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.ludus.dtos.requests.GameDtoRequest;
import com.ludus.dtos.requests.GamePatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.dtos.responses.GameDtoResponse;
import com.ludus.enums.GameGenre;
import com.ludus.enums.GamePlatform;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.ValidationException;
import com.ludus.models.GameModel;
import com.ludus.repositories.GameRepository;
import com.ludus.utils.UtilHelper;

@Service
public class GameService {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private UtilHelper utilHelper;

  public ApiDtoResponse<GameDtoResponse> getAllGames(int page, String genre, String name) {
    if (page < 1) {
      throw new InvalidPageException("Page number must be greater than 0");
    }

    int pageIndex = page - 1;
    Pageable pageable = PageRequest.of(pageIndex, 10);

    Page<GameModel> gamePage;

    GameGenre genreEnum = null;
    if (genre != null) {
      try {
        genreEnum = GameGenre.valueOf(genre.toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        throw new NotFoundException(
            messageSource.getMessage("invalid.genre", null, Locale.getDefault()));
      }
    }

    gamePage = gameRepository.findAll(genreEnum, name, pageable);

    List<GameDtoResponse> gameDTOs =
        gamePage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
    InfoDtoResponse info = utilHelper.buildPageableInfoDto(gamePage, "/games");
    return new ApiDtoResponse<>(info, gameDTOs);

  }

  public GameDtoResponse getGame(Long id) {
    if (id == null || id < 1)
      throw new InvalidIdException();

    GameModel gameModel = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(
        messageSource.getMessage("game.not.found", new Object[] {id}, Locale.getDefault())));
    return convertToDTO(gameModel);
  }

  public void createGame(GameDtoRequest gameDTO, BindingResult bindingResult) {
    validateFields(gameDTO, bindingResult);

    try {
      GameModel gameModel = new GameModel();
      gameModel.setName(gameDTO.name());
      gameModel.setGenre(GameGenre.valueOf(gameDTO.genre().toUpperCase().trim()));
      gameModel.setReleaseYear(gameDTO.releaseYear());
      gameModel.setPlatform(GamePlatform.valueOf(gameDTO.platform().toUpperCase().trim()));
      gameModel.setPrice(BigDecimal.valueOf(gameDTO.price()));
      gameRepository.save(gameModel);
    } catch (Exception e) {
      throw new RetrievalException(
          messageSource.getMessage("game.creation.error", null, Locale.getDefault()));
    }
  }

  public void updateGame(Long id, GamePatchDtoRequest gameDTO, BindingResult bindingResult) {
    if (id == null || id < 1)
      throw new InvalidIdException();
    validatePatchFields(gameDTO, bindingResult);

    GameModel gameModel = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(
        messageSource.getMessage("game.not.found", new Object[] {id}, Locale.getDefault())));
    if (gameDTO.name() != null) {
      gameModel.setName(gameDTO.name());
    }
    if (gameDTO.genre() != null) {
      try {
        gameModel.setGenre(GameGenre.valueOf(gameDTO.genre().toUpperCase().trim()));
      } catch (IllegalArgumentException e) {
        throw new NotFoundException(
            messageSource.getMessage("invalid.genre", null, Locale.getDefault()));
      }
    }
    if (gameDTO.releaseYear() != null) {
      gameModel.setReleaseYear(gameDTO.releaseYear());
    }
    if (gameDTO.platform() != null) {
      try {
        gameModel.setPlatform(GamePlatform.valueOf(gameDTO.platform().toUpperCase().trim()));
      } catch (IllegalArgumentException e) {
        throw new NotFoundException(
            messageSource.getMessage("invalid.platform", null, Locale.getDefault()));
      }
    }
    if (gameDTO.price() != null) {
      gameModel.setPrice(BigDecimal.valueOf(gameDTO.price()));
    }
    if (gameDTO.name() == null && gameDTO.genre() == null && gameDTO.releaseYear() == null
        && gameDTO.platform() == null && gameDTO.price() == null) {
      throw new NotFoundException(
          messageSource.getMessage("game.not.found", new Object[] {id}, Locale.getDefault()));
    }
    if (gameDTO.name() != null) {
      gameModel.setName(gameDTO.name());
    }
    gameRepository.save(gameModel);
  }

  public void deleteGame(Long id) {
    if (id == null || id < 1)
      throw new InvalidIdException();

    try {
      GameModel gameModel = gameRepository.findById(id).orElseThrow(() -> new NotFoundException(
          messageSource.getMessage("game.not.found", new Object[] {id}, Locale.getDefault())));
      gameRepository.delete(gameModel);
    } catch (NotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new RetrievalException(
          messageSource.getMessage("game.deletion.error", null, Locale.getDefault()));
    }
  }

  private GameDtoResponse convertToDTO(GameModel gameModel) {
    return new GameDtoResponse(gameModel.getId(), gameModel.getName(),
        gameModel.getGenre().toString(), gameModel.getReleaseYear(),
        gameModel.getPlatform().toString(), gameModel.getPrice());
  }

  public void validateFields(GameDtoRequest gamedto, BindingResult result) {
    List<String> errors = new ArrayList<>();

    if (result.hasErrors()) {
      errors.addAll(result.getFieldErrors().stream().map(FieldError::getDefaultMessage)
          .collect(Collectors.toList()));
    }

    if (gamedto.genre() != null) {
      try {
        GameGenre.valueOf(gamedto.genre().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add(messageSource.getMessage("invalid.genre", null, Locale.getDefault()));
      }
    }

    if (gamedto.platform() != null) {
      try {
        GamePlatform.valueOf(gamedto.platform().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add(messageSource.getMessage("invalid.platform", null, Locale.getDefault()));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public void validatePatchFields(GamePatchDtoRequest gamedto, BindingResult result) {
    List<String> errors = new ArrayList<>();

    if (result.hasErrors()) {
      errors.addAll(result.getFieldErrors().stream().map(FieldError::getDefaultMessage)
          .collect(Collectors.toList()));
    }

    if (gamedto.genre() != null) {
      try {
        GameGenre.valueOf(gamedto.genre().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add(messageSource.getMessage("invalid.genre", null, Locale.getDefault()));
      }
    }

    if (gamedto.platform() != null) {
      try {
        GamePlatform.valueOf(gamedto.platform().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add(messageSource.getMessage("invalid.platform", null, Locale.getDefault()));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
}
