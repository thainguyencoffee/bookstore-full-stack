package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.author.Author;
import com.bookstore.resourceserver.book.valuetype.*;
import com.bookstore.resourceserver.category.Category;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*Aggregate root*/
@Getter
@Setter
public class Book {

    @Id
    private Long id;
    private String isbn;
    private String title;
    private String publisher;
    private String supplier;
    private String description;
    private Language language;
    private Integer edition;
    @JsonProperty("eBooks")
    private Set<EBook> eBooks = new HashSet<>();
    @JsonProperty("printBooks")
    private Set<PrintBook> printBooks = new HashSet<>();
    private List<String> thumbnails = new ArrayList<>();
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private int version;

    @Embedded.Nullable
    private CategoryRef category;
    private Set<AuthorRef> authors = new HashSet<>();

    public void setCategory(Category c) {
        category = new CategoryRef(c.getId(), c.getName());
    }

    public void addAuthor(Author author) {
        this.authors.add(new AuthorRef(author.getId(), author.getUserInformation().getFullName()));
    }

    public void addAllAuthors(Set<Author> authors) {
        this.authors = authors.stream().map(author -> new AuthorRef(author.getId(), author.getUserInformation().getFullName())).collect(Collectors.toSet());
    }

    public Set<Long> getAuthorIds() {
        return authors.stream().map(AuthorRef::getAuthor).collect(Collectors.toSet());
    }

    public void addEbook(EBook eBook) {
        this.eBooks.add(eBook);
    }

    public void addPrintBook(PrintBook printBook) {
        this.printBooks.add(printBook);
    }

    public EBook getEBookById(Long id) {
        return this.eBooks.stream().filter(eBook -> eBook.getId().equals(id)).findFirst()
                .orElseThrow(() -> new CustomNoResultException(EBook.class, CustomNoResultException.Identifier.ID, id));
    }

    public PrintBook getPrintBookById(Long id) {
        return this.printBooks.stream().filter(printBook -> printBook.getId().equals(id)).findFirst()
                .orElseThrow(() -> new CustomNoResultException(PrintBook.class, CustomNoResultException.Identifier.ID, id));
    }

    public void removeEBookById(Long id) {
        this.getEBooks().remove(getEBookById(id));
    }

    public void removePrintBookById(Long id) {
        this.getPrintBooks().remove(getPrintBookById(id));
    }

}
