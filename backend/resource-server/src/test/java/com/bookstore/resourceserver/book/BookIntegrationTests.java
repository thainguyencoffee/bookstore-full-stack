/*
 * @author thainguyencoffee
 */

package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.author.Author;
import com.bookstore.resourceserver.book.dto.*;
import com.bookstore.resourceserver.book.validator.VietnamesePriceConstraint;
import com.bookstore.resourceserver.category.Category;
import com.bookstore.resourceserver.core.ApiError;
import com.bookstore.resourceserver.core.utils.BeanValidationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BookIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Utils method for creating an {@code Stream<Arguments>} of {@code Invalid Book} to create Book
     *
     * @return an instance of {@code Stream<Arguments>};
     */
    private Stream<Arguments> provideInvalidBooksForCreate() {
        Map<ApiError, BookRequestDto> map = new HashMap<>();
        BeanValidationUtils beanValidationUtils = BeanValidationUtils
                .builder()
                .clazz(BookRequestDto.class)
                .build();

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("isbn", Pattern.class, "", "message")
                        .addInvalid("isbn", NotEmpty.class, "", "message")
                        .addInvalid("category", NotNull.class, null, "message")
                        .addInvalid("authorIds", NotEmpty.class, null, "message")
                        .addInvalid("title", NotEmpty.class, "", "message")
                        .addInvalid("publisher", NotEmpty.class, "", "message")
                        .addInvalid("supplier", NotEmpty.class, "", "message")
                        .addInvalid("edition", Min.class, 0, "message")
                        .build()
                ),
                BookRequestDto.builder()
                        .category(null)
                        .authorIds(null)
                        .isbn("")
                        .title("")
                        .publisher("")
                        .supplier("")
                        .language("VIETNAMESE")
                        .description("DEMO_DESCRIPTION")
                        .edition(0)
                        .build()
        );

        var isbn = generatedNumbers10Digits();
        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("isbn", Pattern.class, isbn + 12345, "message")
                        .addInvalid("category", Min.class, 0, "message")
                        .addInvalid("authorIds", NotEmpty.class, null, "message")
                        .addInvalid("title", NotEmpty.class, null, "message")
                        .addInvalid("publisher", NotEmpty.class, null, "message")
                        .addInvalid("supplier", NotEmpty.class, null, "message")
                        .addInvalid("language", NotEmpty.class, null, "message")
                        .addInvalid("edition", NotNull.class, null, "message")
                        .build()
                ),
                BookRequestDto.builder()
                        .category(0L)
                        .authorIds(null)
                        .isbn(isbn + 12345)
                        .title(null)
                        .publisher(null)
                        .supplier(null)
                        .language(null)
                        .description("DEMO_DESCRIPTION")
                        .edition(null)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("authorIds", NotEmpty.class, Collections.emptySet(), "message")
                        .addInvalid("language", Pattern.class, "NOTHING", "message")
                        .build()
                ),
                BookRequestDto.builder()
                        .category(1L)
                        .authorIds(Collections.emptySet())
                        .isbn(isbn + 123)
                        .title("DEMO_TITLE")
                        .publisher("DEMO_PUBLISHER")
                        .supplier("DEMO_SUPPLIER")
                        .language("NOTHING")
                        .description("DEMO_DESCRIPTION")
                        .edition(1)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("isbn", NotEmpty.class, null, "message")
                        .addInvalid("authorIds", NotEmpty.class, Collections.emptySet(), "message")
                        .addInvalid("language", NotEmpty.class, "", "message")
                        .addInvalid("language", Pattern.class, "", "message")
                        .build()
                ),
                BookRequestDto.builder()
                        .category(1L)
                        .authorIds(Collections.emptySet())
                        .isbn(null)
                        .title("DEMO_TITLE")
                        .publisher("DEMO_PUBLISHER")
                        .supplier("DEMO_SUPPLIER")
                        .language("")
                        .description("DEMO_DESCRIPTION")
                        .edition(1)
                        .build()
        );
        return map.entrySet().stream().map(Arguments::of);
    }

    /**
     * Utils method for creating an {@code Stream<Arguments>} of {@code Invalid Book} to update Book
     *
     * @return an instance of {@code Stream<Arguments>};
     */
    private Stream<Arguments> provideInvalidBooksAndValidBookForUpdate() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());

        Map<ApiError, BookUpdateDto> map = new HashMap<>();
        BeanValidationUtils beanValidationUtils = BeanValidationUtils
                .builder()
                .clazz(BookUpdateDto.class)
                .build();
        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("category", Min.class, 0, "message")
                        .addInvalid("title", Size.class, strMoreThan256Characters, "message")
                        .addInvalid("publisher", Size.class, strMoreThan256Characters, "message")
                        .addInvalid("supplier", Size.class, strMoreThan256Characters, "message")
                        .addInvalid("edition", Min.class, 0, "message")
                        .addInvalid("language", Pattern.class, "NOTHING", "message")
                        .build()
                ),
                BookUpdateDto.builder()
                        .category(0L)
                        .authorIds(null)
                        .title(strMoreThan256Characters)
                        .publisher(strMoreThan256Characters)
                        .supplier(strMoreThan256Characters)
                        .description(strMoreThan256Characters)
                        .language("NOTHING")
                        .edition(0)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("edition", Min.class, 0, "message")
                        .build()
                ),
                BookUpdateDto.builder()
                        .category(null)
                        .authorIds(Set.of(1L))
                        .title("")
                        .publisher("")
                        .supplier("")
                        .description("")
                        .language("")
                        .edition(0)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("edition", Min.class, 0, "message")
                        .build()
                ),
                BookUpdateDto.builder()
                        .category(1L)
                        .authorIds(Set.of(1L))
                        .title(null)
                        .publisher(null)
                        .supplier(null)
                        .description(null)
                        .language(null)
                        .edition(0)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("category", Min.class, 0, "message")
                        .build()
                ),
                BookUpdateDto.builder()
                        .category(0L)
                        .authorIds(Set.of(1L))
                        .title("DEMO")
                        .publisher("DEMO")
                        .supplier("DEMO")
                        .description("DEMO")
                        .language("VIETNAMESE")
                        .edition(1)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("category", Min.class, 0, "message")
                        .build()
                ),
                BookUpdateDto.builder()
                        .category(0L)
                        .authorIds(Set.of(1L))
                        .title("DEMO")
                        .publisher("DEMO")
                        .supplier("DEMO")
                        .description("DEMO")
                        .language("ENGLISH")
                        .edition(1)
                        .build()
        );

        return map.entrySet().stream().map(e -> Arguments.of(e, all.get(i)));
    }

    /**
     * Utils method for creating an {@code Stream<Arguments>} of {@code Invalid EBook}  to create eBook
     *
     * @return an instance of {@code Stream<Arguments>};
     */
    private Stream<Arguments> provideValidBookAndInvalidEBooksForCreate() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());

        Map<ApiError, EBookRequestDto> map = new HashMap<>();
        BeanValidationUtils beanValidationUtils = new BeanValidationUtils(EBookRequestDto.class);

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, null, "message")
                        .addInvalid("format", NotEmpty.class, null, "message")
                        .addInvalid("fileSize", NotNull.class, null, "message")
                        .addInvalid("originalPrice", NotNull.class, null, "message")
                        .addInvalid("numberOfPages", NotNull.class, null, "message")
                        .addInvalid("publicationDate", NotNull.class, null, "message")
                        .addInvalid("releaseDate", NotNull.class, null, "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url(null)
                        .format(null)
                        .fileSize(null)
                        .originalPrice(null)
                        .discountedPrice(null)
                        .numberOfPages(null)
                        .publicationDate(null)
                        .releaseDate(null)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, "", "message")
                        .addInvalid("format", NotEmpty.class, "", "message")
                        .addInvalid("format", Pattern.class, "", "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url("")
                        .format("")
                        .fileSize(12000)
                        .originalPrice(120000L)
                        .discountedPrice(null)
                        .numberOfPages(3000)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, "", "message")
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url("")
                        .format("pdf")
                        .fileSize(12000)
                        .originalPrice(155123L)
                        .discountedPrice(155123L)
                        .numberOfPages(3000)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, "", "message")
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url("")
                        .format("epub")
                        .fileSize(12000)
                        .originalPrice(155123L)
                        .discountedPrice(155123L)
                        .numberOfPages(3000)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, "", "message")
                        .addInvalid("format", Pattern.class, "nothing", "message")
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url("")
                        .format("nothing")
                        .fileSize(12000)
                        .originalPrice(155123L)
                        .discountedPrice(155123L)
                        .numberOfPages(3000)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("url", NotEmpty.class, "", "message")
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 155123L, "message")
                        .build()
                ),
                EBookRequestDto.builder()
                        .url("")
                        .format("epub")
                        .fileSize(12000)
                        .originalPrice(155123L)
                        .discountedPrice(155123L)
                        .numberOfPages(3000)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .build()
        );
        return map.entrySet().stream().map(e -> Arguments.of(e, all.get(i)));
    }

    /**
     * Utils method for creating an {@code Stream<Arguments>} of {@code Invalid EBook}  to update eBook
     *
     * @return an instance of {@code Stream<Arguments>};
     */
    private Stream<Arguments> provideValidBookAndInvalidEBooksForUpdate() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());

        BeanValidationUtils beanValidationUtils = BeanValidationUtils
                .builder()
                .clazz(EBookUpdateDto.class)
                .build();

        Map<ApiError, EBookUpdateDto> map = new HashMap<>();
        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("format", Pattern.class, "pdd", "message")
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123456L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123456L, "message")
                        .build()
                ),
                EBookUpdateDto.builder()
                        .url(null)
                        .fileSize(null)
                        .format("pdd")
                        .originalPrice(123456L)
                        .discountedPrice(123456L)
                        .publicationDate(Instant.now())
                        .releaseDate(null)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123456L, "message")
                        .build()
                ),
                EBookUpdateDto.builder()
                        .url(null)
                        .fileSize(null)
                        .format(null)
                        .originalPrice(null)
                        .discountedPrice(123456L)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123456L, "message")
                        .build()
                ),
                EBookUpdateDto.builder()
                        .url(null)
                        .fileSize(null)
                        .format(null)
                        .originalPrice(123456L)
                        .discountedPrice(null)
                        .build()
        );
        return map.entrySet().stream().map(e -> Arguments.of(e, all.get(i)));
    }

    private Stream<Arguments> provideValidBookAndInvalidPrintBookForCreate() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());

        BeanValidationUtils beanValidationUtils = BeanValidationUtils
                .builder()
                .clazz(PrintBookRequestDto.class)
                .build();

        Map<ApiError, PrintBookRequestDto> map = new HashMap<>();
        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("numberOfPages", NotNull.class, null, "message")
                        .addInvalid("originalPrice", NotNull.class, null, "message")
                        .addInvalid("publicationDate", NotNull.class, null, "message")
                        .addInvalid("releaseDate", NotNull.class, null, "message")
                        .addInvalid("coverType", NotEmpty.class, null, "message")
                        .addInvalid("width", NotNull.class, null, "message")
                        .addInvalid("height", NotNull.class, null, "message")
                        .addInvalid("thickness", NotNull.class, null, "message")
                        .addInvalid("weight", NotNull.class, null, "message")
                        .addInvalid("inventory", NotNull.class, null, "message")
                        .build()
                ),
                PrintBookRequestDto.builder()
                        .numberOfPages(null)
                        .originalPrice(null)
                        .discountedPrice(null)
                        .publicationDate(null)
                        .releaseDate(null)
                        .coverType(null)
                        .width(null)
                        .height(null)
                        .thickness(null)
                        .weight(null)
                        .inventory(null)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("coverType", NotEmpty.class, "", "message")
                        .addInvalid("coverType", Pattern.class, "", "message")
                        .build()
                ),
                PrintBookRequestDto.builder()
                        .numberOfPages(12)
                        .originalPrice(1234456L)
                        .discountedPrice(1234456L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("")
                        .width(12.1)
                        .height(12.1)
                        .thickness(12.1)
                        .weight(12.1)
                        .inventory(12)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("coverType", Pattern.class, "NOTHING", "message")
                        .build()
                ),
                PrintBookRequestDto.builder()
                        .numberOfPages(12)
                        .originalPrice(1234456L)
                        .discountedPrice(1234456L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("NOTHING")
                        .width(12.1)
                        .height(12.1)
                        .thickness(12.1)
                        .weight(12.1)
                        .inventory(12)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .build()
                ),
                PrintBookRequestDto.builder()
                        .numberOfPages(12)
                        .originalPrice(1234456L)
                        .discountedPrice(1234456L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("PAPERBACK")
                        .width(12.1)
                        .height(12.1)
                        .thickness(12.1)
                        .weight(12.1)
                        .inventory(12)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 1234456L, "message")
                        .build()
                ),
                PrintBookRequestDto.builder()
                        .numberOfPages(12)
                        .originalPrice(1234456L)
                        .discountedPrice(1234456L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("HARDCOVER")
                        .width(12.1)
                        .height(12.1)
                        .thickness(12.1)
                        .weight(12.1)
                        .inventory(12)
                        .build()
        );

        return map.entrySet().stream().map(e -> Arguments.of(e, all.get(i)));
    }

    private Stream<Arguments> provideValidBookAndInvalidPrintBookForUpdate() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());

        BeanValidationUtils beanValidationUtils = BeanValidationUtils
                .builder()
                .clazz(PrintBookUpdateDto.class)
                .build();

        Map<ApiError, PrintBookUpdateDto> map = new HashMap<>();
        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .build()
                ),
                PrintBookUpdateDto.builder()
                        .numberOfPages(null)
                        .originalPrice(123445678L)
                        .discountedPrice(123445678L)
                        .publicationDate(null)
                        .releaseDate(null)
                        .coverType(null)
                        .width(null)
                        .height(null)
                        .thickness(null)
                        .weight(null)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("coverType", Pattern.class, "", "message")
                        .build()
                ),
                PrintBookUpdateDto.builder()
                        .numberOfPages(12222)
                        .originalPrice(160000L)
                        .discountedPrice(160000L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("")
                        .width(12.2)
                        .height(12.2)
                        .thickness(12.2)
                        .weight(12.2)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .build()
                ),
                PrintBookUpdateDto.builder()
                        .numberOfPages(12222)
                        .originalPrice(123445678L)
                        .discountedPrice(123445678L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("PAPERBACK")
                        .width(12.2)
                        .height(12.2)
                        .thickness(12.2)
                        .weight(12.2)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .build()
                ),
                PrintBookUpdateDto.builder()
                        .numberOfPages(12222)
                        .originalPrice(123445678L)
                        .discountedPrice(123445678L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("HARDCOVER")
                        .width(12.2)
                        .height(12.2)
                        .thickness(12.2)
                        .weight(12.2)
                        .build()
        );

        map.put(
                beanValidationUtils.buildApiErrorFromAttrOfAnnotation(apiErrorBuilder -> apiErrorBuilder
                        .addInvalid("originalPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .addInvalid("discountedPrice", VietnamesePriceConstraint.class, 123445678L, "message")
                        .addInvalid("coverType", Pattern.class, "NOTHING", "message")
                        .build()
                ),
                PrintBookUpdateDto.builder()
                        .numberOfPages(12222)
                        .originalPrice(123445678L)
                        .discountedPrice(123445678L)
                        .publicationDate(Instant.now())
                        .releaseDate(Instant.now())
                        .coverType("NOTHING")
                        .width(12.2)
                        .height(12.2)
                        .thickness(12.2)
                        .weight(12.2)
                        .build()
        );

        return map.entrySet().stream().map(e -> Arguments.of(e, all.get(i)));
    }

    @Test
    void whenUnauthenticatedGetAllBooksThenOk() {
        webTestClient
                .get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[*].eBooks").isArray()
                .jsonPath("$.content[*].printBooks").isArray();
    }

    @Test
    void whenUnauthenticatedGetAllBooksPagingThenOk() {
        webTestClient
                .get().uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("page", 0)
                        .queryParam("size", 10).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(10)
                .jsonPath("$.content[*].eBooks").isArray()
                .jsonPath("$.content[*].printBooks").isArray();
        ;
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedAndBookAvailableGetBookByIsbnThenOk(Book book) {
        webTestClient
                .get().uri("/books/" + book.getIsbn())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.edition").isEqualTo(book.getEdition())
                .jsonPath("$.authors").isArray()
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().toString())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.category.categoryName").isEqualTo(book.getCategory().getCategoryName())
                .jsonPath("$.eBooks[*]").isArray();
    }


    @Test
    void whenUnauthenticatedAndBookNotAvailableGetBookByIsbnThenNotFound() {
        var isbn = "99999999999";
        webTestClient
                .get().uri("/books/" + isbn)
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenUnauthenticatedCreateBookThen401(Author author, Category category) {
        var bookReq = BookRequestDto.builder()
                .category(category.getId())
                .authorIds(Set.of(author.getId()))
                .isbn(generatedNumbers10Digits())
                .title("DEMO_TITLE")
                .publisher("DEMO_PUBLISHER")
                .supplier("DEMO_SUPPLIER")
                .language("ENGLISH")
                .description("DEMO_DESCRIPTION")
                .edition(2)
                .build();
        webTestClient
                .post()
                .uri("/books")
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenIsCustomerCreateBookThen403(Author author, Category category) {
        var bookReq = BookRequestDto.builder()
                .category(category.getId())
                .authorIds(Set.of(author.getId()))
                .isbn(generatedNumbers10Digits())
                .title("DEMO_TITLE")
                .publisher("DEMO_PUBLISHER")
                .supplier("DEMO_SUPPLIER")
                .language("ENGLISH")
                .description("DEMO_DESCRIPTION")
                .edition(2)
                .build();

        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenAuthenticatedWithValidRoleCreateBookThen201(Author author, Category category) {
        var bookReq = BookRequestDto.builder()
                .category(category.getId())
                .authorIds(Set.of(author.getId()))
                .isbn(generatedNumbers10Digits())
                .title("DEMO_TITLE")
                .publisher("DEMO_PUBLISHER")
                .supplier("DEMO_SUPPLIER")
                .language("ENGLISH")
                .description("DEMO_DESCRIPTION")
                .edition(2)
                .build();

        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(category.getId())
                .jsonPath("$.isbn").isEqualTo(bookReq.isbn())
                .jsonPath("$.title").isEqualTo(bookReq.title())
                .jsonPath("$.authors[0].author").isEqualTo(author.getId())
                .jsonPath("$.authors[0].authorName").isEqualTo(author.getUserInformation().getFullName())
                .jsonPath("$.publisher").isEqualTo(bookReq.publisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.supplier())
                .jsonPath("$.language").isEqualTo(bookReq.language())
                .jsonPath("$.description").isEqualTo(bookReq.description())
                .jsonPath("$.edition").isEqualTo(bookReq.edition());
    }


    @Test
    void whenUnauthenticatedUpdateBookThen401() {
        var bookReq = BookUpdateDto.builder().category(10002L).build();
        var isbn = "1234567891";
        webTestClient
                .patch()
                .uri("/books" + isbn)
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenAuthenticatedWithInvalidRoleUpdateBookThen403() {
        var bookReq = BookUpdateDto.builder().category(10002L).build();
        var isbn = "1234567891";
        webTestClient
                .patch()
                .uri("/books" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideInvalidBooksForCreate")
    void whenIsEmployeeCreateBookInvalid1Then400(Map.Entry<ApiError, BookRequestDto> entry) throws JsonProcessingException {
        String responseBody = webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        assertThat(isEqual).isTrue();
    }


    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategoryAndBook")
    void whenIsEmployeeUpdateBookThen200(Author author, Category category, Book book) {
        Set<Long> authorIds = Set.of(author.getId());
        var bookReq = BookUpdateDto.builder()
                .language(null)
                .publisher("updated publisher")
                .supplier("updated supplier")
                .title("updated title")
                .description("updated description")
                .edition(3)
                .authorIds(authorIds)
                .category(category.getId()).build();

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isbn").isNotEmpty()
                .jsonPath("$.title").isEqualTo(bookReq.title())
                .jsonPath("$.authors").isArray()
                .jsonPath("$.category").isNotEmpty()
                .jsonPath("$.publisher").isEqualTo(bookReq.publisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.supplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(bookReq.description())
                .jsonPath("$.edition").isEqualTo(bookReq.edition())
                .jsonPath("$.eBooks[0].id").isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidBooksAndValidBookForUpdate")
    void whenIsEmployeeUpdateBookInvalidThen400(Map.Entry<ApiError, BookUpdateDto> entry, Book book) throws JsonProcessingException {
        String responseBody = webTestClient.patch()
                .uri("/books/" + book.getIsbn())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        assertThat(isEqual).isTrue();
    }

    @Test
    void whenUnauthenticatedDeleteBookThen401() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenIsCustomerDeleteBookThen403() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleDeleteBookThen204(Book book) {
        webTestClient
                .delete()
                .uri("/books/" + book.getIsbn())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }


    @Test
    void whenUnauthenticatedGetAllEBooksButBookNotFoundThen404() {
        webTestClient
                .get()
                .uri("/books/99999999999/ebooks")
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetAllEBooksThen200(Book book) {
        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/ebooks")
                .exchange().expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[*]").isArray();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetEBookByIdButEBookNotFoundThen404(Book book) {
        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/ebooks/9999999")
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetEBookByIdThen200(Book book) {
        var eBookId = book.getEBooks().iterator().next().getId();
        var ebook = book.getEBookById(eBookId);

        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookId)
                .exchange().expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ebook.getId())
                .jsonPath("$.metadata.url").isEqualTo(ebook.getMetadata().getUrl())
                .jsonPath("$.metadata.format").isEqualTo(ebook.getMetadata().getFormat())
                .jsonPath("$.metadata.fileSize").isEqualTo(ebook.getMetadata().getFileSize())
                .jsonPath("$.properties.price.originalPrice").isEqualTo(ebook.getProperties().getPrice().getOriginalPrice())
                .jsonPath("$.properties.price.discountedPrice").isEqualTo(ebook.getProperties().getPrice().getDiscountedPrice())
                .jsonPath("$.properties.publicationDate").isEqualTo(ebook.getProperties().getPublicationDate().toString())
                .jsonPath("$.properties.releaseDate").isEqualTo(ebook.getProperties().getReleaseDate().toString());
    }


    @Test
    void whenUnauthenticatedCreateEBookThen401() {
        var eBookReq = EBookRequestDto.builder()
                .url("demo.pdf").build();

        webTestClient.post()
                .uri("/books/1234567890" + "/ebooks")
                .body(BodyInserters.fromValue(eBookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerCreateEBookThen403() {
        var eBookReq = EBookRequestDto.builder()
                .url("demo.pdf").build();

        webTestClient
                .post()
                .uri("/books/1234567890/ebooks")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookReq))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeCreateEBookValidThen201(Book book) {
        var eBookReq = EBookRequestDto.builder()
                .url("demo.pdf")
                .format("pdf")
                .fileSize(120000)
                .numberOfPages(300)
                .originalPrice(1200000L)
//                .discountedPrice(100000L)
                .publicationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        webTestClient.post()
                .uri("/books/" + book.getIsbn() + "/ebooks")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.eBooks[1].id").isNotEmpty()
                .jsonPath("$.eBooks[1].metadata.url").isEqualTo(eBookReq.url())
                .jsonPath("$.eBooks[1].metadata.format").isEqualTo(eBookReq.format())
                .jsonPath("$.eBooks[1].metadata.fileSize").isEqualTo(eBookReq.fileSize())
                .jsonPath("$.eBooks[1].properties.numberOfPages").isEqualTo(eBookReq.numberOfPages())
                .jsonPath("$.eBooks[1].properties.price.originalPrice").isEqualTo(eBookReq.originalPrice())
                .jsonPath("$.eBooks[1].properties.price.discountedPrice").isEqualTo(0L)
                .jsonPath("$.eBooks[1].properties.publicationDate").isEqualTo(eBookReq.publicationDate().toString())
                .jsonPath("$.eBooks[1].properties.releaseDate").isEqualTo(eBookReq.releaseDate().toString());
    }


    @Test
    void whenIsEmployeeCreateEBookValidButBookNotFoundThen404() {
        var eBookReq = EBookRequestDto.builder()
                .url("demo.pdf")
                .format("pdf")
                .fileSize(120000)
                .numberOfPages(300)
                .originalPrice(1200000L)
                .discountedPrice(100000L)
                .publicationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        webTestClient.post()
                .uri("/books/9999999999/ebooks")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookReq))
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideValidBookAndInvalidEBooksForCreate")
    void whenIsEmployeeCreateEBookInvalidThen400(Map.Entry<ApiError, EBookRequestDto> entry, Book book) throws JsonProcessingException {
        String responseBody = webTestClient
                .post()
                .uri("/books/" + book.getIsbn() + "/ebooks")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        System.out.println("expect: " + entry.getKey().toString());
        System.out.println("actual: " + objectMapper.readValue(documentContext.jsonString(), ApiError.class).toString());
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        assertThat(isEqual).isTrue();
    }


    @Test
    void whenUnauthenticatedUpdateEBookThen401() {
        var eBookUpdate = EBookUpdateDto.builder()
                .url("url-changed.pdf")
                .format("pdf")
                .fileSize(1100000)
                .numberOfPages(69)
                .originalPrice(600000L)
                .discountedPrice(40000L)
                .publicationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        webTestClient
                .patch()
                .uri("/books/1234567890/ebooks/1")
                .body(BodyInserters.fromValue(eBookUpdate))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerUpdateEBookThen403() {
        var eBookUpdate = EBookUpdateDto.builder()
                .url("url-changed.pdf")
                .format("pdf")
                .fileSize(1100000)
                .numberOfPages(69)
                .originalPrice(600000L)
                .discountedPrice(40000L)
                .publicationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        webTestClient.patch()
                .uri("/books/1234567890/ebooks/1")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookUpdate))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeUpdateEbookValidThen200(Book book) {
        var eBookUpdate = EBookUpdateDto.builder()
                .url("url-changed.pdf")
                .format("pdf")
                .fileSize(1100000)
                .numberOfPages(69)
                .originalPrice(600000L)
                .discountedPrice(40000L)
                .publicationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        var eBookUpdateId = book.getEBooks().iterator().next().getId();

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.eBooks[0].id").isNotEmpty()
                .jsonPath("$.eBooks[0].metadata.url").isEqualTo(eBookUpdate.url())
                .jsonPath("$.eBooks[0].metadata.format").isEqualTo(eBookUpdate.format())
                .jsonPath("$.eBooks[0].metadata.fileSize").isEqualTo(eBookUpdate.fileSize())
                .jsonPath("$.eBooks[0].properties.numberOfPages").isEqualTo(eBookUpdate.numberOfPages())
                .jsonPath("$.eBooks[0].properties.price.originalPrice").isEqualTo(eBookUpdate.originalPrice())
                .jsonPath("$.eBooks[0].properties.price.discountedPrice").isEqualTo(eBookUpdate.discountedPrice())
                .jsonPath("$.eBooks[0].properties.publicationDate").isEqualTo(eBookUpdate.publicationDate().toString())
                .jsonPath("$.eBooks[0].properties.releaseDate").isEqualTo(eBookUpdate.releaseDate().toString());
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeePatchEbookValidThen200(Book book) {
        var eBookUpdate = EBookUpdateDto.builder()
                .url("url-changed.pdf")
                .format("pdf")
//                .fileSize(1100000)
                .numberOfPages(69)
                .originalPrice(600000L)
//                .discountedPrice(40000L)
                .publicationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        EBook eBook = book.getEBooks().iterator().next();
        var eBookUpdateId = eBook.getId();

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.eBooks[0].id").isNotEmpty()
                .jsonPath("$.eBooks[0].metadata.url").isEqualTo(eBookUpdate.url())
                .jsonPath("$.eBooks[0].metadata.format").isEqualTo(eBookUpdate.format())
                .jsonPath("$.eBooks[0].metadata.fileSize").isEqualTo(eBook.getMetadata().getFileSize()) // no change
                .jsonPath("$.eBooks[0].properties.numberOfPages").isEqualTo(eBookUpdate.numberOfPages())
                .jsonPath("$.eBooks[0].properties.price.originalPrice").isEqualTo(eBookUpdate.originalPrice())
                .jsonPath("$.eBooks[0].properties.price.discountedPrice").isEqualTo(eBook.getProperties().getPrice().getDiscountedPrice()) // no change
                .jsonPath("$.eBooks[0].properties.publicationDate").isEqualTo(eBookUpdate.publicationDate().toString())
                .jsonPath("$.eBooks[0].properties.releaseDate").isEqualTo(eBookUpdate.releaseDate().toString());
    }

    @ParameterizedTest
    @MethodSource("provideValidBookAndInvalidEBooksForUpdate")
    void whenIsEmployeeUpdateEBookInvalidThen400(Map.Entry<ApiError, EBookUpdateDto> entry, Book book) throws JsonProcessingException {
        var eBookId = book.getEBooks().iterator().next().getId();

        String responseBody = webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        assertThat(isEqual).isTrue();
    }


    @Test
    void whenIsEmployeeUpdateEBookValidButBookNotFoundThen404() {
        var eBookReq = EBookRequestDto.builder()
                .url("demo.pdf")
                .format("pdf")
                .fileSize(120000)
                .numberOfPages(300)
                .originalPrice(1200000L)
                .discountedPrice(100000L)
                .publicationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        webTestClient.patch()
                .uri("/books/9999999999/ebooks/1")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(eBookReq))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUnauthenticatedDeleteEBookThen401() {
        webTestClient.delete()
                .uri("/books/9999999999/ebooks/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerDeleteEbookThen403() {
        webTestClient.delete()
                .uri("/books/9999999999/ebooks/1")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeDeleteEBookNotFoundThen404(Book book) {
        webTestClient.delete()
                .uri("/books/" + book.getIsbn() + "/ebooks/9999999")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenIsEmployeeDeleteEBookWithBookNotFoundThen404() {
        webTestClient.delete()
                .uri("/books/9999999999/ebooks/1")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeDeleteEBookThen204(Book book) {
        var eBookUpdateId = book.getEBooks().iterator().next().getId();

        webTestClient.delete()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/ebooks/" + eBookUpdateId)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUnauthenticatedGetAllPrintBooksButBookNotFoundThen404() {
        webTestClient
                .get()
                .uri("/books/99999999999/print-books")
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetAllPrintBooksThen200(Book book) {
        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/print-books")
                .exchange().expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[*]").isArray();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetPrintBookByIdButPrintBookNotFoundThen404(Book book) {
        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/print-books/9999999")
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenUnauthenticatedGetPrintBookByIdThen200(Book book) {
        var printBookId = book.getPrintBooks().iterator().next().getId();
        var printBook = book.getPrintBookById(printBookId);

        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/print-books/" + printBookId)
                .exchange().expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(printBook.getId())
                .jsonPath("$.coverType").isEqualTo(printBook.getCoverType().name())
                .jsonPath("$.inventory").isEqualTo(printBook.getInventory())
                .jsonPath("$.properties.price.originalPrice").isEqualTo(printBook.getProperties().getPrice().getOriginalPrice())
                .jsonPath("$.properties.price.discountedPrice").isEqualTo(printBook.getProperties().getPrice().getDiscountedPrice())
                .jsonPath("$.properties.publicationDate").isEqualTo(printBook.getProperties().getPublicationDate().toString())
                .jsonPath("$.properties.releaseDate").isEqualTo(printBook.getProperties().getReleaseDate().toString())
                .jsonPath("$.measure.width").isEqualTo(printBook.getMeasure().getWidth())
                .jsonPath("$.measure.height").isEqualTo(printBook.getMeasure().getHeight())
                .jsonPath("$.measure.thickness").isEqualTo(printBook.getMeasure().getThickness())
                .jsonPath("$.measure.weight").isEqualTo(printBook.getMeasure().getWeight());
    }

    @Test
    void whenUnauthenticatedCreatePrintBookThen401() {
        var printBookReq = PrintBookRequestDto.builder()
                .inventory(1).build();

        webTestClient.post()
                .uri("/books/1234567890" + "/print-books")
                .body(BodyInserters.fromValue(printBookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerCreatePrintBookThen403() {
        var printBookReq = PrintBookRequestDto.builder()
                .inventory(1).build();

        webTestClient
                .post()
                .uri("/books/1234567890/print-books")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookReq))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeCreatePrintBookValidThen201(Book book) {
        var printBookReq = PrintBookRequestDto.builder()
                .coverType("PAPERBACK")
                .originalPrice(1200000L)
                .publicationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .numberOfPages(300)
                .inventory(1)
                .width(12.2)
                .height(12.2)
                .thickness(12.2)
                .weight(12.2)
                .build();

        webTestClient.post()
                .uri("/books/" + book.getIsbn() + "/print-books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.printBooks[1].id").isNotEmpty()
                .jsonPath("$.printBooks[1].measure.width").isEqualTo(printBookReq.width())
                .jsonPath("$.printBooks[1].measure.height").isEqualTo(printBookReq.height())
                .jsonPath("$.printBooks[1].measure.thickness").isEqualTo(printBookReq.thickness())
                .jsonPath("$.printBooks[1].measure.weight").isEqualTo(printBookReq.weight())
                .jsonPath("$.printBooks[1].properties.numberOfPages").isEqualTo(printBookReq.numberOfPages())
                .jsonPath("$.printBooks[1].properties.price.originalPrice").isEqualTo(printBookReq.originalPrice())
                .jsonPath("$.printBooks[1].properties.price.discountedPrice").isEqualTo(0L)
                .jsonPath("$.printBooks[1].properties.publicationDate").isEqualTo(printBookReq.publicationDate().toString())
                .jsonPath("$.printBooks[1].properties.releaseDate").isEqualTo(printBookReq.releaseDate().toString());
    }


    @Test
    void whenIsEmployeeCreatePrintBookValidButBookNotFoundThen404() {
        var printBookReq = PrintBookRequestDto.builder()
                .coverType("PAPERBACK")
                .originalPrice(1200000L)
                .publicationDate(Instant.now().plus(15, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .numberOfPages(300)
                .inventory(1)
                .width(12.2)
                .height(12.2)
                .thickness(12.2)
                .weight(12.2)
                .build();

        webTestClient.post()
                .uri("/books/9999999999/print-books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookReq))
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideValidBookAndInvalidPrintBookForCreate")
    void whenIsEmployeeCreatePrintBookInvalidThen400(Map.Entry<ApiError, PrintBookRequestDto> entry, Book book) throws JsonProcessingException {
        String responseBody = webTestClient
                .post()
                .uri("/books/" + book.getIsbn() + "/print-books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        System.out.println("expect: " + entry.getKey().toString());
        System.out.println("actual: " + objectMapper.readValue(documentContext.jsonString(), ApiError.class).toString());
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        assertThat(isEqual).isTrue();
    }


    @Test
    void whenUnauthenticatedUpdatePrintBookThen401() {
        var printBookUpdate = PrintBookUpdateDto.builder()
                .numberOfPages(15)
                .build();

        webTestClient
                .patch()
                .uri("/books/1234567890/print-books/1")
                .body(BodyInserters.fromValue(printBookUpdate))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerUpdatePrintBookThen403() {
        var printBookUpdate = PrintBookUpdateDto.builder()
                .numberOfPages(15)
                .build();

        webTestClient.patch()
                .uri("/books/1234567890/print-books/1")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookUpdate))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeUpdatePrintBookValidThen200(Book book) {
        var printBookUpdate = PrintBookUpdateDto.builder()
                .numberOfPages(999)
                .originalPrice(150000L)
                .discountedPrice(120000L)
                .publicationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .coverType("HARDCOVER")
                .width(50.2)
                .height(50.2)
                .thickness(50.2)
                .weight(50.2)
                .build();


        var printBookUpdateId = book.getPrintBooks().iterator().next().getId();

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/print-books/" + printBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.printBooks[0].id").isNotEmpty()
                .jsonPath("$.printBooks[0].measure.width").isEqualTo(printBookUpdate.width())
                .jsonPath("$.printBooks[0].measure.height").isEqualTo(printBookUpdate.height())
                .jsonPath("$.printBooks[0].measure.thickness").isEqualTo(printBookUpdate.thickness())
                .jsonPath("$.printBooks[0].measure.weight").isEqualTo(printBookUpdate.weight())
                .jsonPath("$.printBooks[0].properties.numberOfPages").isEqualTo(printBookUpdate.numberOfPages())
                .jsonPath("$.printBooks[0].properties.price.originalPrice").isEqualTo(printBookUpdate.originalPrice())
                .jsonPath("$.printBooks[0].properties.price.discountedPrice").isEqualTo(printBookUpdate.discountedPrice())
                .jsonPath("$.printBooks[0].properties.publicationDate").isEqualTo(printBookUpdate.publicationDate().toString())
                .jsonPath("$.printBooks[0].properties.releaseDate").isEqualTo(printBookUpdate.releaseDate().toString());
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeePatchPrintBookValidThen200(Book book) {
        var printBookUpdate = PrintBookUpdateDto.builder()
                .numberOfPages(999)
                .originalPrice(150000L)
                .discountedPrice(120000L)
                .publicationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .coverType("HARDCOVER")
//                .width(50.2)
//                .height(50.2)
//                .thickness(50.2)
//                .weight(50.2)
                .build();

        var printBook = book.getPrintBooks().iterator().next();
        var printBookUpdateId = printBook.getId();

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/print-books/" + printBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.category").isEqualTo(book.getCategory().getCategory())
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(book.getAuthors().iterator().next().getAuthor())
                .jsonPath("$.authors[0].authorName").isEqualTo(book.getAuthors().iterator().next().getAuthorName())
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().name())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.edition").isEqualTo(book.getEdition())

                .jsonPath("$.printBooks[0].id").isNotEmpty()
                .jsonPath("$.printBooks[0].measure.width").isEqualTo(printBook.getMeasure().getWidth())
                .jsonPath("$.printBooks[0].measure.height").isEqualTo(printBook.getMeasure().getHeight())
                .jsonPath("$.printBooks[0].measure.thickness").isEqualTo(printBook.getMeasure().getThickness())
                .jsonPath("$.printBooks[0].measure.weight").isEqualTo(printBook.getMeasure().getWeight())
                .jsonPath("$.printBooks[0].properties.numberOfPages").isEqualTo(printBookUpdate.numberOfPages())
                .jsonPath("$.printBooks[0].properties.price.originalPrice").isEqualTo(printBookUpdate.originalPrice())
                .jsonPath("$.printBooks[0].properties.price.discountedPrice").isEqualTo(printBookUpdate.discountedPrice())
                .jsonPath("$.printBooks[0].properties.publicationDate").isEqualTo(printBookUpdate.publicationDate().toString())
                .jsonPath("$.printBooks[0].properties.releaseDate").isEqualTo(printBookUpdate.releaseDate().toString());
    }

    @ParameterizedTest
    @MethodSource("provideValidBookAndInvalidPrintBookForUpdate")
    void whenIsEmployeeUpdatePrintBookInvalidThen400(Map.Entry<ApiError, PrintBookUpdateDto> entry, Book book) throws JsonProcessingException {
        var eBookId = book.getEBooks().iterator().next().getId();

        String responseBody = webTestClient
                .patch()
                .uri("/books/" + book.getIsbn() + "/print-books/" + eBookId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(entry.getValue()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        boolean isEqual = areErrorsEqualIgnoringOrder(entry.getKey(), objectMapper.readValue(documentContext.jsonString(), ApiError.class));
        System.out.println("expect: " + entry.getKey().toString());
        System.out.println("actual: " + objectMapper.readValue(documentContext.jsonString(), ApiError.class).toString());
        assertThat(isEqual).isTrue();
    }


    @Test
    void whenIsEmployeeUpdatePrintBookValidButBookNotFoundThen404() {
        var printBookUpdate = PrintBookUpdateDto.builder()
                .numberOfPages(999)
                .originalPrice(150000L)
                .discountedPrice(120000L)
                .publicationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .releaseDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .coverType("HARDCOVER")
                .width(50.2)
                .height(50.2)
                .thickness(50.2)
                .weight(50.2)
                .build();

        webTestClient.patch()
                .uri("/books/9999999999/print-books/1")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(printBookUpdate))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUnauthenticatedDeletePrintBookThen401() {
        webTestClient.delete()
                .uri("/books/9999999999/print-books/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenIsCustomerDeletePrintBookThen403() {
        webTestClient.delete()
                .uri("/books/9999999999/print-books/1")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeDeletePrintBookNotFoundThen404(Book book) {
        webTestClient.delete()
                .uri("/books/" + book.getIsbn() + "/print-books/9999999")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenIsEmployeeDeletePrintBookWithBookNotFoundThen404() {
        webTestClient.delete()
                .uri("/books/9999999999/print-books/1")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenIsEmployeeDeletePrintBookThen204(Book book) {
        var printBookUpdateId = book.getPrintBooks().iterator().next().getId();

        webTestClient.delete()
                .uri("/books/" + book.getIsbn() + "/print-books/" + printBookUpdateId)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/books/" + book.getIsbn() + "/print-books/" + printBookUpdateId)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void whenUnauthenticatedUploadThumbnailsBookThen401() {
        String isbn = "1234567890";
        webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void whenAuthenticatedWithInvalidRoleUploadThumbnailsBookThen403() {
        String isbn = "1234567890";
        webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookThenOK(Book book) throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = book.getIsbn();

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = BookUpdateDto.builder()
                .thumbnails(thumbnails).build();
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(2);
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookAndRemoveOldThumbnailsThenOK(Book book) throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = book.getIsbn();

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = BookUpdateDto.builder()
                .thumbnails(thumbnails).build();
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(2);

        List<String> thumbnails2 = uploadThumbnails(isbn);
        thumbnails2.add(thumbnails.get(0));
        req = BookUpdateDto.builder()
                .thumbnails(thumbnails2).build();
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(3);

        // Phía digital ocean ở thư mục thumbnails của book này nên chỉ có 3 files
    }


    private List<String> uploadThumbnails(String isbn) throws com.fasterxml.jackson.core.JsonProcessingException {
        String responseRaw = webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseRaw, new TypeReference<>() {
        });
    }


    private static String generatedNumbers10Digits() {
        var number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(number);
    }


    private boolean areErrorsEqualIgnoringOrder(ApiError expected, ApiError actual) throws JsonProcessingException {
        if (expected.getErrors().size() != actual.getErrors().size()) {
            return false;
        }
        List<ApiError.ErrorInfo> sortedExpectedErrors = new ArrayList<>(expected.getErrors());
        List<ApiError.ErrorInfo> sortedActualErrors = new ArrayList<>(actual.getErrors());

        sortedExpectedErrors.sort(Comparator.comparing(ApiError.ErrorInfo::getProperty)
                .thenComparing(ApiError.ErrorInfo::getMessage));
        sortedActualErrors.sort(Comparator.comparing(ApiError.ErrorInfo::getProperty)
                .thenComparing(ApiError.ErrorInfo::getMessage));

        String e = objectMapper.writeValueAsString(sortedExpectedErrors);
        String a = objectMapper.writeValueAsString(sortedActualErrors);
        return e.equals(a);
    }

    private static final String strMoreThan256Characters = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris at ultrices augue. Maecenas rhoncus, enim sed scelerisque placerat, mi justo accumsan lorem, in venenatis sapien dolor quis felis. Morbi porttitor eros vitae facilisis sollicitudin. Maecenas tincidunt nunc et libero vulputate suscipit. Morbi vel orci nec libero condimentum tincidunt et eu est. Ut mauris massa, semper ut aliquet non, ultricies ac ipsum. Nam ullamcorper urna id nisl luctus maximus mattis et tortor. Phasellus cursus ipsum ut pulvinar dapibus. Ut lobortis tortor id massa ultricies hendrerit.
            
            Integer pretium nulla sapien, quis dapibus sapien faucibus a. Maecenas ultricies posuere est, id vulputate ligula mattis nec. Ut ac quam ac augue luctus imperdiet. Nulla vel mauris egestas, faucibus erat at, cursus purus. Sed rhoncus interdum erat eu efficitur. Morbi leo sapien, ultrices vitae fermentum vel, interdum sed urna. Nunc felis urna, blandit at diam at, sollicitudin euismod eros. Mauris tristique sed felis vel accumsan. Maecenas aliquet nulla ut tristique aliquam. Nulla imperdiet euismod libero, at molestie sem. Praesent a rutrum lorem, eget consectetur nunc. Suspendisse tincidunt tortor et velit mollis efficitur. Sed auctor nibh rhoncus arcu sollicitudin, vel malesuada tellus eleifend. Sed vel dui congue, egestas arcu at, ultricies lacus. Aenean eu porttitor eros.
            
            Cras vestibulum ac tortor in blandit. Pellentesque mauris quam, hendrerit et libero at, tempor molestie leo. Donec at mauris fringilla, fermentum libero eu, rutrum odio. Praesent commodo elit ac dictum commodo. Praesent sapien purus, luctus eget tempor ac, consectetur eu lacus. Sed aliquet ullamcorper pulvinar. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Proin vehicula vehicula nisl, vitae dignissim magna consectetur a. Aliquam ultrices hendrerit elementum. Etiam sed interdum felis, eget dignissim libero. Mauris pellentesque dictum venenatis. Etiam tempor imperdiet semper. Vivamus quis sem volutpat est dignissim mattis eu at ex. Curabitur consequat, lorem non tincidunt hendrerit, nibh justo maximus nibh, quis molestie eros tellus non tortor.
            
            Sed at sapien odio. Proin venenatis tempor dui, nec ullamcorper turpis euismod a. Integer eros tellus, accumsan in laoreet eget, sodales at diam. Vivamus vitae venenatis arcu. Nullam sit amet nunc enim. Sed maximus venenatis tincidunt. Sed eget faucibus lorem. Praesent eget mattis augue. Morbi luctus placerat enim, vel feugiat libero luctus eget. Nulla congue odio sit amet velit fermentum, at bibendum sapien volutpat. Nunc felis est, interdum nec risus eget, viverra suscipit augue. Fusce accumsan accumsan odio vitae interdum. Curabitur quis rhoncus felis. Fusce sollicitudin suscipit massa at sollicitudin.
            
            Aliquam aliquet eu arcu nec congue. Sed dictum neque ex, sit amet sollicitudin nisl facilisis euismod. Duis vel augue eleifend, mattis arcu sit amet, faucibus elit. Nulla facilisi. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla fermentum turpis tellus, non scelerisque orci euismod quis. Aliquam quis tristique augue, non cursus turpis. Aliquam id tortor a turpis pharetra lobortis. Fusce et congue augue. In elementum dolor sit amet metus venenatis interdum. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur tristique justo id tellus mattis malesuada. Suspendisse vitae sodales libero, eu interdum leo. In vel velit non ipsum dictum elementum. Vestibulum sit amet rutrum odio.
            """;

}