package com.effitrack.server.repository;

import com.effitrack.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTableNumber(String tableNumber);

    Boolean existsByTableNumber(String tableNumber);
}
