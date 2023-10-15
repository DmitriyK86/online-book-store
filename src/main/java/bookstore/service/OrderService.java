package bookstore.service;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlaceOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusRequestDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    void placeOrder(Authentication authentication, PlaceOrderRequestDto requestDto);

    List<OrderResponseDto> getOrdersHistory(Authentication authentication, Pageable pageable);

    List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItem(Long orderId, Long itemId);

    OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);
}
