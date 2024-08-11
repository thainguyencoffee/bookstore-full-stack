package com.bookstore.resourceserver.jdbc;

import com.bookstore.resourceserver.book.Category;
import com.bookstore.resourceserver.book.CategoryRepository;
import com.bookstore.resourceserver.book.emailpreferences.EmailPreferences;
import com.bookstore.resourceserver.book.emailpreferences.EmailPreferencesRepository;
import com.bookstore.resourceserver.book.emailpreferences.EmailTopic;
import com.bookstore.resourceserver.core.config.DataConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@ActiveProfiles("integration")
public class EmailPreferencesCategoryJdbcTests {

    @Autowired
    private EmailPreferencesRepository emailPreferencesRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void whenEmailPreferencesFindAllCategoriesThenOK() {
        var category1 = new Category();
        category1.setName("Fiction");
        category1.setParentId(null);
        categoryRepository.save(category1);

        var category2 = new Category();
        category2.setName("Demo category 2");
        category2.setParentId(category1.getId());
        categoryRepository.save(category2);

        var emailPreferences = new EmailPreferences();
        emailPreferences.setEmail("demo@gmail.com");
        emailPreferences.setFirstName("Demo");
        emailPreferences.setLastName("User");
        emailPreferences.setEmailTopics(List.of(EmailTopic.NEW_RELEASES));
        emailPreferences.addCategory(category1);
        emailPreferencesRepository.save(emailPreferences);

        assertThat(emailPreferences.getCategories().size()).isEqualTo(1);
        for (EmailPreferences i : emailPreferencesRepository.findAll()) {
            Iterable<Category> categoryIterable = categoryRepository.findAllById(i.getCategoryIds());
            assertThat(categoryIterable).isNotNull();
            System.out.println("List categories by email preferences: " + categoryIterable);
        }
        for (Category i : categoryRepository.findAll()) {
            Set<EmailPreferences> emailPreferencesSet = emailPreferencesRepository.findByCategoryId(i.getId());
            assertThat(emailPreferencesSet).isNotNull();
            System.out.println("List email preferences by category: " + emailPreferencesSet);
        }
    }

    @Test
    void whenEmailPreferencesFindAllCategoriesThenOK2() {
        var category1 = new Category();
        category1.setName("Fiction");
        category1.setParentId(null);
        categoryRepository.save(category1);

        var category2 = new Category();
        category2.setName("Demo category 2");
        category2.setParentId(null);
        categoryRepository.save(category2);

        var category3 = new Category();
        category3.setName("Demo category 2");
        category3.setParentId(category1.getParentId());
        categoryRepository.save(category3);

        var emailPreferences = new EmailPreferences();
        emailPreferences.setEmail("demo@gmail.com");
        emailPreferences.setFirstName("Demo");
        emailPreferences.setLastName("User");
        emailPreferences.setEmailTopics(List.of(EmailTopic.NEW_RELEASES));
        emailPreferences.addCategory(category1);
        emailPreferencesRepository.save(emailPreferences);


        Iterable<Category> categories = categoryRepository.findAllById(emailPreferences.getCategoryIds());
        assertThat(categories).isNotNull();
        for (Category category : categories) {
            assertThat(category.getId()).isEqualTo(category1.getId());
        }
    }

    @AfterEach
    void tearDown() {
        emailPreferencesRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
