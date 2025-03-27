package com.ludus.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.ludus.dto.GameDTO;
import com.ludus.enums.GameGenre;
import com.ludus.enums.GamePlatform;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.RetrievalException;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.ValidationException;
import com.ludus.models.GameModel;
import com.ludus.repository.GameRepository;

@Service
public class GameService {
  
  @Autowired
  private GameRepository gameRepository;

  public List<GameDTO> getAllGames() {
    try {
      List<GameModel> gameModels = gameRepository.findAll();
      return gameModels.stream().map(this::convertToDTO).collect(Collectors.toList());
    } catch (Exception e) {
      throw new RetrievalException("Error retrieving games");
    }
  }

  public GameDTO getGame(Long id) {
    if (id == null || id < 1)
      throw new InvalidIdException();
    try {
      GameModel gameModel = gameRepository.findById(id).orElseThrow(() -> new NotFoundException());
      return convertToDTO(gameModel);
    } catch (NotFoundException e) {
      throw new NotFoundException("Game not found");
    } catch (Exception e) {
      throw new InvalidIdException();
    }
  }

  public void createGame(GameDTO gameDTO, BindingResult bindingResult) {
    validateFields(gameDTO, bindingResult);

    GameModel gameModel = new GameModel();
    gameModel.setName(gameDTO.name());
    gameModel.setGenre(GameGenre.valueOf(gameDTO.genre().toUpperCase().trim()));
    gameModel.setReleaseYear(gameDTO.releaseYear());
    gameModel.setPlatform(GamePlatform.valueOf(gameDTO.platform().toUpperCase().trim()));
    gameModel.setPrice(BigDecimal.valueOf(gameDTO.price()));
    gameRepository.save(gameModel);

  }

  public void updateGame(Long id, GameDTO gameDTO, BindingResult bindingResult) {
    if (id == null || id < 1)
      throw new InvalidIdException();
    validateFields(gameDTO, bindingResult);

    GameModel gameModel = gameRepository.findById(id).orElseThrow(() -> new NotFoundException());
    gameModel.setName(gameDTO.name());
    gameModel.setGenre(GameGenre.valueOf(gameDTO.genre().toUpperCase().trim()));
    gameModel.setReleaseYear(gameDTO.releaseYear());
    gameModel.setPlatform(GamePlatform.valueOf(gameDTO.platform().toUpperCase().trim()));
    gameModel.setPrice(BigDecimal.valueOf(gameDTO.price()));
    gameRepository.save(gameModel);
  }

  public void deleteGame(Long id) {
    if (id == null || id < 1)
      throw new InvalidIdException();
    GameModel gameModel =
        gameRepository.findById(id).orElseThrow(() -> new NotFoundException("Game not found"));

    gameRepository.delete(gameModel);
  }

  private GameDTO convertToDTO(GameModel gameModel) {
    return new GameDTO(gameModel.getName(), gameModel.getGenre().toString(),
        gameModel.getReleaseYear(), gameModel.getPlatform().toString(),
        gameModel.getPrice().floatValue());
  }

  public void validateFields(GameDTO gamedto, BindingResult result) {
    List<String> errors = new ArrayList<>();

    if (result.hasErrors()) {
      errors.addAll(result.getFieldErrors().stream().map(FieldError::getDefaultMessage)
          .collect(Collectors.toList()));
    }

    if (gamedto.genre() != null) {
      try {
        GameGenre.valueOf(gamedto.genre().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add("Invalid genre");
      }
    }

    if (gamedto.platform() != null) {
      try {
        GamePlatform.valueOf(gamedto.platform().toUpperCase().trim());
      } catch (IllegalArgumentException e) {
        errors.add("Invalid platform");
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

}
