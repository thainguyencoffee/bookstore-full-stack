<<<<<<<< HEAD:backend/resource-server/src/main/java/com/bookstore/resourceserver/category/CategoryRepository.java
package com.bookstore.resourceserver.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
========
package com.bookstore.resourceserver.book.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
>>>>>>>> origin:backend/resource-server/src/main/java/com/bookstore/resourceserver/book/category/CategoryRepository.java
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {

    List<Category> findAll(Pageable pageable);

    @Query(value = """
            with recursive subcategoriess as (
                   select c.*
                   from categories c where id = :id
               
                   union all
               
                   select c.*
                   from categories c
                            inner join subcategoriess sc on c.parent_id = sc.id
               )
               
               select * from subcategoriess sc where sc.id <> :id;
            """)
    List<Category> findAllSubCategoriesById(@Param("id") Long id);

}
