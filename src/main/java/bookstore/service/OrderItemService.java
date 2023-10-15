package bookstore.service;

import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.model.OrderItem;

public interface OrderItemService {
    OrderItem save(OrderItem orderItem);

    OrderItemResponseDto findByOrderIdAndId(Long orderId, Long itemId);
}
