
package com.example.springEcom.model.dtos;

import java.math.BigDecimal;

public record OrderItemRequest(
    Long productId,
    Integer quantity,
    BigDecimal price
) {}
