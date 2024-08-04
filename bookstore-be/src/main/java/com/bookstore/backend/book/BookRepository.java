package com.bookstore.backend.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Page<Book> findAll(Pageable pageable);

    Optional<Book> findByIsbn(String query);

    Page<Book> findAllByAuthorContaining(String query, Pageable pageable);

    Page<Book> findAllByTitleContaining(String query, Pageable pageable);

    Page<Book> findAllByPublisherContaining(String query, Pageable pageable);

    Page<Book> findAllBySupplierContaining(String query, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from books where isbn = :isbn")
    void deleteByIsbn(@Param("isbn") String isbn);

    @Query("update books set inventory = inventory - :quantity where isbn = :isbn")
    @Modifying
    @Transactional
    void updateInventoryByIsbn(@Param("isbn") String isbn, @Param("quantity") Integer quantity);

    @Query("select * from books b order by b.purchases desc limit :size offset :page * :size")
    List<Book> findAllBookBestSeller(int size, int page);

//    @Query("select * from books b order by b.purchases desc limit :size offset :page * :size")
    Page<Book> findAllByOrderByPurchasesDesc(Pageable pageable);

    @Query("select b.* from books b where b.category_id = :categoryId LIMIT :limit OFFSET :offset")
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") int limit, @Param("offset") int offset);

}
