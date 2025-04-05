package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.enums.GameGenre;
import com.ludus.models.GameModel;

public interface GameRepository extends JpaRepository<GameModel, Long> {

    Page<GameModel> findByGenreAndName(GameGenre genre, String name, Pageable pageable);

    Page<GameModel> findByGenre(GameGenre genre, Pageable pageable);

    Page<GameModel> findByName(String name, Pageable pageable);

}
