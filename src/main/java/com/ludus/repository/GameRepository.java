package com.ludus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.models.GameModel;

public interface GameRepository extends JpaRepository<GameModel, Long> {

}
