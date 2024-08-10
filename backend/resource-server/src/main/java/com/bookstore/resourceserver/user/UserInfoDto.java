package com.bookstore.resourceserver.user;

import java.time.Instant;
import java.util.List;

/**
 * @param username a unique identifier for the resource owner in the token (sub claim by default)
 * @param email OpenID email claim
 * @param roles Spring authorities resolved for the authentication in the security context
 * @param exp seconds from 1970-01-01T00:00:00Z UTC until the specified UTC date/time when the access token expires
 */
public record UserInfoDto(String username, String email, List<String> roles, Instant exp) {

    public static final UserInfoDto ANONYMOUS =
            new UserInfoDto("", "", List.of(), Instant.EPOCH);

}
