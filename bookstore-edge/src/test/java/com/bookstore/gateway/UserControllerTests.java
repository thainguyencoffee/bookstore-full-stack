package com.bookstore.gateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.gateway.config.SecurityConfig;
import com.bookstore.gateway.user.User;
import com.bookstore.gateway.user.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;


@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

     @Test
     void whenNotAuthenticatedThenReturn401() {
         webTestClient
                 .get()
                 .uri("/user")
                 .exchange()
                 .expectStatus().isUnauthorized();
     }
    @Test
    void whenAuthenticatedThenReturnUser() {
        var expectedUser = new User(
                "michaeljackson",
                "Nguyen",
                "Thai",
                "Male",
                "2004-03-11",
                "nguyennt11032004@gmail.com",
                true,
                null,
                List.of("employee", "customer"));

        webTestClient
                .mutateWith(configureMockOidcLogin(expectedUser))
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(
            User expectedUser
    ) {
        return SecurityMockServerConfigurers.mockOidcLogin()
                .idToken(builder -> builder
                .claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username())
                .claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName())
                .claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName())
                .claim(StandardClaimNames.GENDER, expectedUser.gender())
                .claim(StandardClaimNames.BIRTHDATE, expectedUser.birthdate())
                .claim(StandardClaimNames.EMAIL, expectedUser.email())
                .claim(StandardClaimNames.EMAIL_VERIFIED, expectedUser.emailVerified())
                .claim("roles", expectedUser.roles())
                );
    }

}
