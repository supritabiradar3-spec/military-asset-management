package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByBase(Base base);
    List<User> findByBaseId(Long baseId);
}
