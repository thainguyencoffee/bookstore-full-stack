package com.bookstorefullstack.bookstore.purchaseorder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends ListCrudRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByIdAndCreatedBy(UUID id, String createdBy);

    Page<PurchaseOrder> findAllByCreatedBy(String createdBy, Pageable pageable);

    Optional<PurchaseOrder> findByIdAndOtp(UUID id, long otp);

    Page<PurchaseOrder> findAllByStatus(OrderStatus status, Pageable pageable);
}
