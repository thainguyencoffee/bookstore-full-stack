package com.bookstore.resourceserver.book.emailpreferences;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("email_preferences_category")
@Getter
@Setter
@AllArgsConstructor
public class EmailCategoryRef {

    private Long category;

    private String name;

}
