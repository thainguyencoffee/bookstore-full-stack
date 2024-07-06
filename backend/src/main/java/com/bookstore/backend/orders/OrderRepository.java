package com.bookstore.backend.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {

    Page<Order> findAllByCreatedBy(String createdBy, Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
}
