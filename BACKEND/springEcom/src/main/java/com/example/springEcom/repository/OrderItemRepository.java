
package com.example.springEcom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springEcom.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_Id(Long orderId);
    List<OrderItem> findByOrder_OrderId(String orderId);
}

