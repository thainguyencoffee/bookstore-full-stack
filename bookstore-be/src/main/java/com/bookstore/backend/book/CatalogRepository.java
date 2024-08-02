package com.bookstore.backend.book;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CatalogRepository extends ListCrudRepository<Catalog, Long> {

    @Query("""
            with recursive subcatalogs as (
                select id, name, parent_id
                from catalogs where id = :id
            
                union all
            
                select c.id, c.name, c.parent_id
                from catalogs c
                inner join subcatalogs sc on c.parent_id = sc.id
            )
            
            select * from subcatalogs sc where sc.id <> :id;
            
            """)
    Set<Catalog> findAllSubCatalogsById(@Param("id") Long id);

}
