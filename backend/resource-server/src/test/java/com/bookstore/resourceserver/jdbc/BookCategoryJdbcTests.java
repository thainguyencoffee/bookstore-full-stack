package com.bookstore.resourceserver.jdbc;

import com.bookstore.resourceserver.book.*;
import com.bookstore.resourceserver.core.config.DataConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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

        List<Category> subCategories = categoryRepository.findAllSubCategoriesById(10000L);
        assertThat(subCategories).hasSize(2);
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
        return book;
    }

    private Category buildCategoryExample(Long parentId, String name) {
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(name);
        return category;
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

}
