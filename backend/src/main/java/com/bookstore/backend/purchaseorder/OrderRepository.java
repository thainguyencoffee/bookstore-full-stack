package com.bookstore.backend.purchaseorder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> {

    Optional<Order> findByIdAndCreatedBy(UUID id, String createdBy);

    Page<Order> findAllByCreatedBy(String createdBy, Pageable pageable);

    Optional<Order> findByIdAndOtp(UUID id, long otp);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
}
