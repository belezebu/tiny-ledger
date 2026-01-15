package com.example.tinyledger.user.repository;

import com.example.tinyledger.common.exception.EntityAlreadyExistsException;
import com.example.tinyledger.user.domain.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserInMemoryRepository implements UserRepository {

    private Map<UUID, User> users;

    public UserInMemoryRepository() {
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized User save(User user) {
        if (users.containsKey(user.id())) {
            throw EntityAlreadyExistsException.userAlreadyExists(user.id());
        }

        users.put(user.id(), user);
        return user;
    }

    @Override
    public Optional<User> get(UUID id) {
        return Optional.ofNullable(users.get(id));
    }
}
