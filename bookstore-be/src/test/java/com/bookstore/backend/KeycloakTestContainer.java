package com.bookstore.backend;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class KeycloakTestContainer {

    private static KeycloakContainer container;

    public static KeycloakContainer getInstance() {
        if (container == null) {
            container = new KeycloakContainer("quay.io/keycloak/keycloak:23.0")
                    .withRealmImportFile("bookstore-realm.json");
            container.start();
        }
        return container;
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> getInstance().getAuthServerUrl() + "/realms/bookstore");
    }

}