
package com.example.springEcom.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springEcom.model.Order;
import com.example.springEcom.model.OrderItem;
import com.example.springEcom.model.Product;
import com.example.springEcom.model.dtos.OrderRequest;
import com.example.springEcom.model.dtos.OrderItemRequest;
import com.example.springEcom.model.dtos.OrderResponse;
import com.example.springEcom.model.dtos.OrderItemResponse;
import com.example.springEcom.repository.OrderRepository;
import com.example.springEcom.repository.OrderItemRepository;
import com.example.springEcom.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final ProductRepository productRepo;

    public OrderService(
            OrderRepository orderRepo,
            OrderItemRepository itemRepo,
            ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException(
                "Order must contain at least one item");
        }

        Order order = new Order();
        order.setOrderId(
            UUID.randomUUID().toString()
                .substring(0, 8)
                .toUpperCase()
        );
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());
        orderRepo.save(order);

        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (OrderItemRequest req : request.items()) {

            if (req.quantity() == null || req.quantity() <= 0) {
                throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
            }

            Integer productId = Math.toIntExact(req.productId());

            Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Product not found: " + productId));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(req.quantity());

            BigDecimal totalPrice = BigDecimal.valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(req.quantity()));

            item.setTotalPrice(totalPrice);

            itemRepo.save(item);
            orderItems.add(item);
        }

        return toResponse(order, orderItems);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(String orderId) {

        Order order = orderRepo.findByOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Order not found: " + orderId));

        List<OrderItem> items = itemRepo.findAll().stream()
            .filter(item -> item.getOrder().getOrderId().equals(order.getOrderId()))
            .toList();

        return toResponse(order, items);
    }

    private OrderResponse toResponse(Order order, List<OrderItem> orderItems) {

        List<OrderItemResponse> items = orderItems.stream()
            .map(item -> new OrderItemResponse(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getTotalPrice()
            ))
            .toList();

        return new OrderResponse(
            order.getOrderId(),
            order.getCustomerName(),
            order.getEmail(),
            order.getStatus(),
            order.getOrderDate(),
            items
        );
    }
    @Transactional(readOnly = true)
public List<OrderResponse> getAllOrders() {
    return orderRepo.findAll()
        .stream()
        .map(order -> {
            List<OrderItem> items =
                itemRepo.findByOrder_OrderId(order.getOrderId());

            return toResponse(order, items);
        })
        .toList();
}
}

