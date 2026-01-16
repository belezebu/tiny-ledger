package com.example.tinyledger.user.controller;

import com.example.tinyledger.common.controller.response.CreateEntityResponse;
import com.example.tinyledger.user.controller.request.CreateUserRequest;
import com.example.tinyledger.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse create(@Valid @RequestBody CreateUserRequest createUserRequest) {
        var user = userService.createUser(createUserRequest.getFirstName(), createUserRequest.getLastName(), createUserRequest.getEmailAddress());
        return new CreateEntityResponse(user.id());
    }
}
