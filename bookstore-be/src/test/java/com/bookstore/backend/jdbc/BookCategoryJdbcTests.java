package com.bookstore.backend.jdbc;

import com.bookstore.backend.book.*;
import com.bookstore.backend.core.config.DataConfig;
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
public class BookCategoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testRelationship() {
        var isbn = "0987654321";

        var catalog = buildCategoryExample(null, "Computer Science");
        categoryRepository.save(catalog);
        var book = buildBookExample(isbn);
        book.setCategory(catalog);
        bookRepository.save(book);

        // from `category` get all books
        bookRepository.findAllByCategoryId(catalog.getId(), 10, 0).forEach(b -> {
            b.getCategory().getCategoryId().equals(catalog.getId());
        });
    }

    @Test
    void testFindAllSubCategoriesByIdThenSuccess() {
        Set<Category> subCategories = categoryRepository.findAllSubCategoriesById(10L);
        subCategories.forEach(c -> assertThat(c.getParentId()).isEqualTo(10));
        assertThat(subCategories).hasSize(3);
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

    private Category buildCategoryExample(Long parentId, String name) {
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(name);
        return category;
    }
}
