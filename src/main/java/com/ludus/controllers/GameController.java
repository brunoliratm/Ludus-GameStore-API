package com.ludus.controllers;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ludus.dto.GameDTO;
import com.ludus.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/api/games")
public class GameController {

  @Autowired
  private GameService gameService;

  @Operation(summary = "Get All Games")
  @GetMapping()
  public ResponseEntity<List<GameDTO>> getGames() {
    List<GameDTO> games = gameService.getGames();
    return ResponseEntity.ok(games);
  }

  @Operation(summary = "Get a Game by ID")
  @GetMapping("/{id}")
  public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
    GameDTO game = gameService.getGame(id);
    return ResponseEntity.ok(game);
  }

  @Operation(summary = "Create a New Game")
  @PostMapping()
  public ResponseEntity<Void> createGame(@RequestBody @Valid GameDTO gameDTO, BindingResult bindingResult) {
    gameService.createGame(gameDTO, bindingResult);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Update an Existing Game")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGame(@PathVariable Long id, @RequestBody @Valid GameDTO gameDTO, BindingResult bindingResult) {
    gameService.updateGame(id, gameDTO, bindingResult);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Delete an Existing Game")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
    gameService.deleteGame(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
