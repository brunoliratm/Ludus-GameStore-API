package com.ludus.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ludus.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByEmail(String email);
    
    Page<UserModel> findByActiveTrue(Pageable pageable);

    Page<UserModel> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

}
