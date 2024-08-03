package com.bookstore.backend.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {

    List<Category> findAll(Pageable pageable);

    @Query("""
            with recursive subcategoriess as (
                select id, name, parent_id
                from categories where id = :id
            
                union all
            
                select c.id, c.name, c.parent_id
                from categories c
                inner join subcategoriess sc on c.parent_id = sc.id
            )
            
            select * from subcategoriess sc where sc.id <> :id;
            
            """)
    Set<Category> findAllSubCategoriesById(@Param("id") Long id);

}
