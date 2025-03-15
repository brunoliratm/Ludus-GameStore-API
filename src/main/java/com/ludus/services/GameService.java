package com.ludus.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ludus.dto.GameDTO;
import com.ludus.exceptions.GameNotFoundException;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.models.GameModel;
import com.ludus.repository.GameRepository;

@Service
public class GameService {

  @Autowired
  private GameRepository gameRepository;

  public List<GameDTO> getGames() {
    try {
      List<GameModel> gameModels = gameRepository.findAll();
      return gameModels.stream()
          .map(this::convertToDTO)
          .collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error retrieving games", e);
    }
  }

  public GameDTO getGame(Long id) {
    if (id == null || id < 1) throw new InvalidIdException();
    try {
      GameModel gameModel = gameRepository.findById(id)
          .orElseThrow(() -> new GameNotFoundException());
      return convertToDTO(gameModel);
    } catch (GameNotFoundException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw new InvalidIdException();
    }
  }

  private GameDTO convertToDTO(GameModel gameModel) {
    return new GameDTO(
        gameModel.getId(),
        gameModel.getName(),
        gameModel.getGenre().toString(),
        gameModel.getReleaseYear(),
        gameModel.getPlatform().toString(),
        gameModel.getPrice().floatValue());
  }

}
