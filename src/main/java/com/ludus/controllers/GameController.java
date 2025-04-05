package com.ludus.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ludus.dtos.requests.GameDtoRequest;
import com.ludus.dtos.requests.GamePatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.GameDtoResponse;
import com.ludus.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/${api.version}/games")
public class GameController {

  @Autowired
  private GameService gameService;

  @Operation(summary = "Get All Games")
  @GetMapping()
  public ResponseEntity<ApiDtoResponse<GameDtoResponse>> getAllGames(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) String name
  ) {
    ApiDtoResponse<GameDtoResponse> games = gameService.getAllGames(
      page, genre, name
    );
    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  @Operation(summary = "Get a Game by ID")
  @GetMapping("/{id}")
  public ResponseEntity<GameDtoResponse> getGame(@PathVariable Long id) {
    GameDtoResponse game = gameService.getGame(id);
    return new ResponseEntity<>(game, HttpStatus.OK);
  }

  @Operation(summary = "Create a New Game")
  @PostMapping()
  public ResponseEntity<Void> createGame(@RequestBody @Valid GameDtoRequest gameDTO, BindingResult bindingResult) {
    gameService.createGame(gameDTO, bindingResult);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Update an Existing Game")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGame(@PathVariable Long id, @RequestBody @Valid GamePatchDtoRequest gameDTO, BindingResult bindingResult) {
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
