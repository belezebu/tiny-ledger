package com.example.tinyledger.user.domain;

import java.util.UUID;

public record User(UUID id, String firstName, String lastName, String emailAddress) {}
