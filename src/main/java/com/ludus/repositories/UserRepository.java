package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import com.ludus.models.UserModel;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserDetails findUserByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    @Query("""
            SELECT u FROM UserModel u
            WHERE (:name IS NULL OR LOWER(CAST(u.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
            AND u.active = true
            """)
    Page<UserModel> findAll(@Param("name")String name, Pageable pageable);

}
