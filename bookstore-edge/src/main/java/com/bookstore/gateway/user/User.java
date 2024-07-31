package com.bookstore.gateway.user;

import java.util.List;

public record User (
        String username,
        String firstName,
        String lastName,
        String gender,
        String birthdate,
        String email,
        Boolean emailVerified,
        List<String> pictures,
        List<String> roles
) {
}
