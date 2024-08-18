<<<<<<<< HEAD:backend/resource-server/src/main/java/com/bookstore/resourceserver/category/Category.java
package com.bookstore.resourceserver.category;
========
package com.bookstore.resourceserver.book.category;
>>>>>>>> origin:backend/resource-server/src/main/java/com/bookstore/resourceserver/book/category/Category.java

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("category")
@Getter
@Setter
public class Category {

    @Id
    private Long id;
    private String name;
    private String thumbnail;
    private Long parentId;
    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedAt;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private int version;

    public void setParent(Category category) {
        this.parentId = category.getId();
    }
}
