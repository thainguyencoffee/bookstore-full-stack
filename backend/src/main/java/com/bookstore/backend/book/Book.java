package com.bookstore.backend.book;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Table("books")
public class Book {

    @Id
    private Long id;

    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    private String isbn;

    @NotBlank(message = "The title of book must not be null or blank.")
    @Size(max = 255, message = "The title too long")
    private String title;

    @NotBlank(message = "The author of book must not be null or blank.")
    @Size(max = 255, message = "The author name is too long")
    private String author;

    @NotBlank(message = "The publisher of book must not be null or blank.")
    @Size(max = 255, message = "The publisher name is too long")
    private String publisher;

    @NotBlank(message = "The supplier of book must not be null or blank.")
    @Size(max = 255, message = "The supplier name is too long")
    private String supplier;

    private String description;

    @NotNull(message = "The price of book must not be null.")
    @Positive(message = "Value of price must greater than zero")
    private Long price;

    @NotNull(message = "The inventory of book must not be null.")
    @Min(value = 0, message = "The inventory must greater than 0")
    private Integer inventory;

    @NotNull(message = "The language of book must not be null.")
    private Language language;

    @NotNull(message = "The book cover type must not be null.")
    private CoverType coverType;

    @NotNull(message = "The number of pages of book must not be null.")
    @Min(value = 3, message = "The number of pages must greater than 3")
    @Max(value = 3000, message = "The number of pages must less than 3000")
    private Integer numberOfPages;

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    @NotNull(message = "The measure of book must not be null.")
    @Valid
    private Measure measure;

    private List<String> photos;

    private Integer purchases;

    @CreatedDate
    private Instant createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Instant lastModifiedAt;

    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private int version;

    public Book() {
    }

    public Book(Long id, String isbn, String title, String author,
                String publisher, String supplier, String description,
                Long price, Integer inventory, Language language,
                CoverType coverType, Integer numberOfPages,
                Measure measure, List<String> photos, Integer purchases, Instant createdAt,
                String createdBy, Instant lastModifiedAt, String lastModifiedBy, int version) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.supplier = supplier;
        this.description = description;
        this.price = price;
        this.inventory = inventory;
        this.language = language;
        this.coverType = coverType;
        this.numberOfPages = numberOfPages;
        this.measure = measure;
        this.photos = photos;
        this.purchases = purchases;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
    }

    public static Book of(String isbn, String title, String author, String publisher, String supplier, Long price, int inventory, Language language, CoverType coverType, Integer numberOfPages, Measure measure) {
        return new Book(null, isbn, title, author, publisher, supplier, null, price, inventory, language, coverType, numberOfPages, measure, null, 0, null, null, null, null, 0);
    }

}
