package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.book.valuetype.EBookFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("ebook")
@Getter
@Setter
public class EBook {
    @Id
    private Long id;
    @MappedCollection(keyColumn = "ebook")
    private Set<EBookFile> eBookFiles = new HashSet<>();
    @Embedded.Nullable
    private BookProperties properties;

    public void addEBookFile(EBookFile eBookFile) {
        this.eBookFiles.add(eBookFile);
    }
}
