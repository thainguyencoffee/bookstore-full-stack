package com.bookstore.resourceserver.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {

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
