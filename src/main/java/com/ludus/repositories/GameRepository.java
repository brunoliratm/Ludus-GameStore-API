package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ludus.enums.GameGenre;
import com.ludus.models.GameModel;

public interface GameRepository extends JpaRepository<GameModel, Long> {

    @Query("""
    SELECT g FROM GameModel g
    WHERE (:genre IS NULL OR g.genre = :genre)
    AND (:name IS NULL OR LOWER(CAST(g.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    """)
    Page<GameModel> findAll(@Param("genre") GameGenre genre, @Param("name") String name, Pageable pageable);

}
