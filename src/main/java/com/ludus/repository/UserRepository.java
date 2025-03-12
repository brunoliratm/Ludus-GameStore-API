package com.ludus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ludus.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    UserModel findByEmail(String email);
}
