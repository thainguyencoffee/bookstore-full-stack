package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.book.valuetype.EBookFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Optional;
import java.util.function.Supplier;

@Table("ebook")
@Getter
@Setter
public class EBook {
    @Id
    private Long id;
    @Embedded.Nullable
    private EBookFile metadata;
    @Embedded.Nullable
    private BookProperties properties;

    public EBook updateIfPresent(Supplier<EBook> eBookFunction) {

        return this;
    }
}
