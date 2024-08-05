package com.bookstore.backend;


import com.bookstore.backend.book.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestsBase {

    protected static KeycloakToken employeeToken;
    protected static KeycloakToken customerToken;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        KeycloakTestContainer.keycloakProperties(registry);
    }

    @BeforeAll
    static void generateAccessToken() {
        WebClient webClient = WebClient.builder()
                .baseUrl(KeycloakTestContainer.getInstance().getAuthServerUrl() +
                        "/realms/bookstore/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        employeeToken = authenticateWith("employee", "1", webClient);
        customerToken = authenticateWith("user", "1", webClient);
    }

    protected static class KeycloakToken {
        private final String accessToken;

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

    private static KeycloakToken authenticateWith(
            String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "edge-service")
                        .with("client_secret", "cT5pq7W3XStcuFVQMhjPbRj57Iqxcu4n")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }


    @BeforeEach
    void setUp() {
        // Foreign Books
        Category foreignCategory = new Category();
        foreignCategory.setId(10000L);
        foreignCategory.setName("Foreign Books");
        categoryRepository.save(foreignCategory);

        Category javaCategory = new Category();
        javaCategory.setId(10001L);
        javaCategory.setName("Java Books");
        javaCategory.setParent(foreignCategory);
        categoryRepository.save(javaCategory);

        Category springCategory = new Category();
        springCategory.setId(10002L);
        springCategory.setName("Spring Books");
        springCategory.setParent(javaCategory);
        categoryRepository.save(springCategory);

        // Vietnamese Books
        Category vietnameseCategory = new Category();
        vietnameseCategory.setId(10003L);
        vietnameseCategory.setName("Vietnamese Books");
        categoryRepository.save(vietnameseCategory);

        Category schoolbookCategory = new Category();
        schoolbookCategory.setId(10004L);
        schoolbookCategory.setName("Schoolbook");
        schoolbookCategory.setParent(vietnameseCategory);
        categoryRepository.save(schoolbookCategory);

        Category grade1Category = new Category();
        grade1Category.setId(10005L);
        grade1Category.setName("Grade 1");
        grade1Category.setParent(schoolbookCategory);
        categoryRepository.save(grade1Category);

        Category grade2Category = new Category();
        grade2Category.setId(10006L);
        grade2Category.setName("Grade 2");
        grade2Category.setParent(schoolbookCategory);
        categoryRepository.save(grade2Category);

        Book springBootInActionBook = new Book();
        springBootInActionBook.setIsbn("1234567890");
        springBootInActionBook.setTitle("Spring boot in action");
        springBootInActionBook.setAuthor("Craig Walls");
        springBootInActionBook.setPublisher("Manning");
        springBootInActionBook.setSupplier("Manning");
        springBootInActionBook.setPrice(1000000L);
        springBootInActionBook.setLanguage(Language.ENGLISH);
        springBootInActionBook.setCoverType(CoverType.PAPERBACK);
        springBootInActionBook.setNumberOfPages(300);
        springBootInActionBook.setPurchases(10);
        springBootInActionBook.setInventory(100);
        springBootInActionBook.setDescription("Spring boot in action");
        springBootInActionBook.setMeasure(new Measure(300, 400, 100, 170));
        springBootInActionBook.setCategory(springCategory);
        bookRepository.save(springBootInActionBook);

        Book grade1MathBook = new Book();
        grade1MathBook.setIsbn("1234567891");
        grade1MathBook.setTitle("Grade 1 Math");
        grade1MathBook.setAuthor("Nguyen Van A");
        grade1MathBook.setPublisher("NXB Giao Duc");
        grade1MathBook.setSupplier("NXB Giao Duc");
        grade1MathBook.setPrice(100000L);
        grade1MathBook.setLanguage(Language.VIETNAMESE);
        grade1MathBook.setCoverType(CoverType.PAPERBACK);
        grade1MathBook.setNumberOfPages(100);
        grade1MathBook.setPurchases(10);
        grade1MathBook.setInventory(100);
        grade1MathBook.setDescription("Grade 1 Math");
        grade1MathBook.setMeasure(new Measure(300, 400, 100, 170));
        grade1MathBook.setCategory(grade1Category);
        bookRepository.save(grade1MathBook);

    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

}