package com.hklapstore.backend.repository;

import com.hklapstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    // Prefer these safer variants in code to avoid NonUniqueResultException if duplicates exist temporarily
    boolean existsByUsername(String username);
    User findTopByUsernameOrderByIdAsc(String username);
    
    // Legacy signature (avoid using when duplicates might exist)
    User findByUsername(String username);
}
