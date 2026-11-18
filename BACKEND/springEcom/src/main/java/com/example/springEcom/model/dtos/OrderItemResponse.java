
package com.example.springEcom.model.dtos;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productName,
    int quantity,
    BigDecimal totalPrice
) {}

