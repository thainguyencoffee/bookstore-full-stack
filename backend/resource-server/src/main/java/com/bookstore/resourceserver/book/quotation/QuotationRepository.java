package com.bookstore.resourceserver.book.quotation;

import org.springframework.data.repository.ListCrudRepository;

public interface QuotationRepository extends ListCrudRepository<Quotation, Long> {
}
