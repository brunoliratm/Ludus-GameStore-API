package com.ludus.exceptions;

public class GameNotFoundException extends RuntimeException {
  public GameNotFoundException() {
    super("Game not found");
  }

}
