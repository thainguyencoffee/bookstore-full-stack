package com.bookstore.resourceserver.author;

import com.bookstore.resourceserver.core.valuetype.UserInformation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "author")
@Getter
@Setter
public class Author {

    @Id
    private Long id;
    @Embedded.Nullable
    private UserInformation userInformation;
    private String jobTitle;
    private String about;
}
