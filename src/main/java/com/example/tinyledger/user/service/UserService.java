package com.example.tinyledger.user.service;

import com.example.tinyledger.common.exception.EntityNotFoundException;
import com.example.tinyledger.user.domain.User;
import com.example.tinyledger.user.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String firstName, String lastName, String emailAddress) {
        var user = new User(UUID.randomUUID(), firstName, lastName, emailAddress);
        return this.userRepository.save(user);
    }

    public User getUser(UUID id) {
        return this.userRepository.get(id).orElseThrow(() -> EntityNotFoundException.userNotFound(id));
    }
}
