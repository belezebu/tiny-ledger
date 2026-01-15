package com.example.tinyledger.user.repository;

import com.example.tinyledger.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> get(UUID id);
}
