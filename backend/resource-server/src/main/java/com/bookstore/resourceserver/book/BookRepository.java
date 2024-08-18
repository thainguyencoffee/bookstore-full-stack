package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.book.dto.view.BookSalesView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends ListCrudRepository<Book, Long> {

    Page<Book> findAll(Pageable pageable);

    Optional<Book> findByIsbn(String query);

    void deleteByIsbn(String isbn);

    @Query("select b.* from book b where b.category = :categoryId LIMIT :limit OFFSET :offset")
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("select b.isbn, b.title, sum(eb.purchases) + sum(pb.purchases) as total_purchases\n" +
            "from book b\n" +
            "         join ebook eb on b.id = eb.book\n" +
            "         join print_book pb on b.id = pb.book\n" +
            "group by b.id\n" +
            "order by total_purchases desc limit :limit")
    List<BookSalesView> getTopBookSales(@Param("limit") int limit);

}
