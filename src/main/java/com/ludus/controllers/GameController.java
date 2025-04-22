package com.ludus.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.ludus.dtos.requests.GameDtoRequest;
import com.ludus.dtos.requests.GamePatchDtoRequest;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.GameDtoResponse;
import com.ludus.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/${api.version}/games")
@Tag(name = "Games", description = "Endpoints for game management")
public class GameController {

  @Autowired
  private GameService gameService;

  @Operation(
    summary = "Get All Games", 
    description = "Retrieves a paginated list of games with optional filtering by genre and name",
    responses = {
        @ApiResponse(responseCode = "200", description = "Games found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid Page", value = "{\"message\": \"Page number must be greater than 0\"}"),
                @ExampleObject(name = "Invalid Page Format", value = "{\"message\": \"Invalid Page format: must be a number\"}"),
                @ExampleObject(name = "Invalid Genre", value = "{\"message\": \"Invalid genre. Valid options are: ACTION, ADVENTURE, FIGHTING, HORROR, MMORPG, RACING, RPG, SHOOTER, SIMULATION, SPORTS, STRATEGY, SURVIVAL, OTHER\"}")
            })
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Unauthorized access. Authentication required.\"}")
            )
        )
    }
  )
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

  @Operation(
    summary = "Get a Game by ID", 
    description = "Retrieves a specific game by its ID",
    responses = {
        @ApiResponse(responseCode = "200", description = "Game found successfully"),
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
        @ApiResponse(responseCode = "404", description = "Game not found", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Game not found with id: 123\"}")
            )
        )
    }
  )
  @GetMapping("/{id}")
  public ResponseEntity<GameDtoResponse> getGame(@PathVariable Long id) {
    GameDtoResponse game = gameService.getGame(id);
    return new ResponseEntity<>(game, HttpStatus.OK);
  }

  @Operation(
    summary = "Create a New Game", 
    description = "Creates a new game with the provided details",
    responses = {
        @ApiResponse(responseCode = "201", description = "Game created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid game data provided", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Validation Error", value = "{\"error\": \"Validation failed\", \"details\": [\"Game name cannot be blank\", \"Genre cannot be blank\", \"Release year cannot be null\", \"Platform cannot be blank\", \"Price cannot be null\"]}"),
                @ExampleObject(name = "Invalid Genre", value = "{\"error\": \"Validation failed\", \"details\": [\"Invalid genre. Valid options are: ACTION, ADVENTURE, FIGHTING, HORROR, MMORPG, RACING, RPG, SHOOTER, SIMULATION, SPORTS, STRATEGY, SURVIVAL, OTHER\"]}"),
                @ExampleObject(name = "Invalid Platform", value = "{\"error\": \"Validation failed\", \"details\": [\"Invalid platform. Valid options are: PC, PLAYSTATION, XBOX, NINTENDO, MOBILE, OTHER\"]}")
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
        @ApiResponse(responseCode = "500", description = "Error creating game", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Error creating game\"}")
            )
        )
    }
  )
  @PostMapping()
  public ResponseEntity<Void> createGame(@RequestBody @Valid GameDtoRequest gameDTO, BindingResult bindingResult) {
    gameService.createGame(gameDTO, bindingResult);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(
    summary = "Update an Existing Game", 
    description = "Updates an existing game with the provided details",
    responses = {
        @ApiResponse(responseCode = "200", description = "Game updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid game data provided", 
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "Invalid ID", value = "{\"message\": \"Invalid ID\"}"),
                @ExampleObject(name = "Invalid Genre", value = "{\"error\": \"Validation failed\", \"details\": [\"Invalid genre. Valid options are: ACTION, ADVENTURE, FIGHTING, HORROR, MMORPG, RACING, RPG, SHOOTER, SIMULATION, SPORTS, STRATEGY, SURVIVAL, OTHER\"]}"),
                @ExampleObject(name = "Invalid Platform", value = "{\"error\": \"Validation failed\", \"details\": [\"Invalid platform. Valid options are: PC, PLAYSTATION, XBOX, NINTENDO, MOBILE, OTHER\"]}")
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
        @ApiResponse(responseCode = "404", description = "Game not found", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Game not found with id: 123\"}")
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error updating game", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Error updating game\"}")
            )
        )
    }
  )
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateGame(@PathVariable Long id, @RequestBody @Valid GamePatchDtoRequest gameDTO, BindingResult bindingResult) {
    gameService.updateGame(id, gameDTO, bindingResult);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
    summary = "Delete an Existing Game", 
    description = "Permanently deletes a game from the database",
    responses = {
        @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
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
        @ApiResponse(responseCode = "404", description = "Game not found", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Game not found with id: 123\"}")
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error deleting game", 
            content = @Content(mediaType = "application/json", 
                examples = @ExampleObject(value = "{\"message\": \"Error deleting game\"}")
            )
        )
    }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
    gameService.deleteGame(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
