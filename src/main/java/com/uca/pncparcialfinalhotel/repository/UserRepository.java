package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<User> findByRole(UserRole role);
}
