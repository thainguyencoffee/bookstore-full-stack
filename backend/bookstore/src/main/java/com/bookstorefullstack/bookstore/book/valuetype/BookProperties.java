package com.bookstorefullstack.bookstore.book.valuetype;

import com.bookstorefullstack.bookstore.core.valuetype.Price;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.Instant;

@Getter
@Setter
public class BookProperties {

    private Integer purchases;
    private Integer numberOfPages;
    @Embedded.Nullable
    private Price price;
    private Instant publicationDate;
    private Instant releaseDate;

}
