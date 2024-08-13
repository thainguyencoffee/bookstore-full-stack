package com.bookstore.resourceserver.book.emailpreferences;

import com.bookstore.resourceserver.book.category.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Table("email_preferences")
@Getter
@Setter
public class EmailPreferences {

    @Id
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<EmailTopic> emailTopics = new ArrayList<>();
    private Set<EmailCategoryRef> categories = new HashSet<>();

    public void addCategory(Category category) {
        this.categories.add(new EmailCategoryRef(category.getId(), category.getName()));
    }

    public void addAllCategories(Set<Category> categories) {
        for (Category category : categories) {
            addCategory(category);
        }
    }

    public Set<Long> getCategoryIds() {
        return this.categories.stream().map(EmailCategoryRef::getCategory).collect(Collectors.toSet());
    }

}
