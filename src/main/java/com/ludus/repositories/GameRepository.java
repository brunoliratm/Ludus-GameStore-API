package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.models.GameModel;

public interface GameRepository extends JpaRepository<GameModel, Long> {

    Page<GameModel> findByGenreAndName(String genre, String name, Pageable pageable);

    Page<GameModel> findByGenre(String genre, Pageable pageable);

    Page<GameModel> findByName(String name, Pageable pageable);

}
