
package com.example.springEcom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springEcom.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
}

