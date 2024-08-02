package com.bookstore.backend.jdbc;

import com.bookstore.backend.book.*;
import com.bookstore.backend.core.config.DataConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@ActiveProfiles("integration")
public class BookCatalogJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        catalogRepository.deleteAll();
    }

    @Test
    void testRelationship() {
        var isbn = "1234567890";

        var catalog = buildCatalogExample(null, "Computer Science");
        catalogRepository.save(catalog);
        var book = buildBookExample(isbn);
        book.addCatalog(catalog);
        bookRepository.save(book);

        // find all catalogs of the book
        catalogRepository.findAllById(book.getCatalogIds()).forEach(c -> {
            assertThat(book.getCatalogIds()).contains(c.getId());
            assertThat(c.getName()).isEqualTo(catalog.getName());
        });

        // from `catalog` get all books
        bookRepository.findAllByCatalogId(catalog.getId()).forEach(b -> {
            boolean contains = b.getCatalogIds().contains(catalog.getId());
            Assertions.assertThat(contains).isTrue();
        });
    }

    @Test
    void testFindAllSubCatalogsByIdThenSuccess() {
        var vietnameseCatalog = buildCatalogExample(null, "Vietnamese Literature");
        catalogRepository.save(vietnameseCatalog);
        var sub1 = buildCatalogExample(vietnameseCatalog.getId(), "Tiểu thuyết của Lan Rùa");
        catalogRepository.save(sub1);
        var sub2 = buildCatalogExample(vietnameseCatalog.getId(), "Truyện ngắn của Nguyễn Nhật Ánh");
        catalogRepository.save(sub2);
        var sub3 = buildCatalogExample(vietnameseCatalog.getId(), "Sách Giáo Khoa");
        catalogRepository.save(sub3);

        Set<Catalog> subCatalogs = catalogRepository.findAllSubCatalogsById(vietnameseCatalog.getId());
        subCatalogs.forEach(c -> assertThat(c.getParentId()).isEqualTo(vietnameseCatalog.getId()));
        assertThat(subCatalogs).hasSize(3);
    }

    private Book buildBookExample(String isbn) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle("The Art of Computer Programming");
        book.setAuthor("Donald Knuth");
        book.setPublisher("Addison-Wesley");
        book.setSupplier("Amazon");
        book.setDescription("The Art of Computer Programming is a comprehensive monograph " +
                "written by Donald Knuth that covers many kinds of programming algorithms and their analysis.");
        book.setPrice(10000L);
        book.setInventory(100);
        book.setPurchases(0);
        book.setLanguage(Language.ENGLISH);
        book.setCoverType(CoverType.HARDCOVER);
        book.setNumberOfPages(1000);
        book.setMeasure(new Measure(300, 400, 100, 170));
        book.setThumbnails(Set.of("https://images-na.ssl-images-amazon.com/images/I/41Z5GZzZomL._SX331_BO1,204,203,200_.jpg"));
        return book;
    }

    private Catalog buildCatalogExample(Long parentId, String name) {
        Catalog catalog = new Catalog();
        catalog.setParentId(parentId);
        catalog.setName(name);
        return catalog;
    }
}
