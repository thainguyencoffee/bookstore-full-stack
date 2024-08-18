package com.bookstore.resourceserver.category;

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
