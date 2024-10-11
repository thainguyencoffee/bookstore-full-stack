package com.bookstorefullstack.bookstore.emailpreference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("email_preference_category")
@Getter
@Setter
@AllArgsConstructor
public class EmailCategoryRef {

    private Long category;

    private String name;

}
