package bookstore.controller;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlaceOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusRequestDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void placeOrder(Authentication authentication,
                           @RequestBody @Valid PlaceOrderRequestDto requestDto) {
        orderService.placeOrder(authentication, requestDto);
    }

    @GetMapping
    @Operation(summary = "Get orders history", description = "Retrieve user's order history")
    public List<OrderResponseDto> getOrdersHistory(Authentication authentication,
                                                   @ParameterObject Pageable pageable) {
        return orderService.getOrdersHistory(authentication, pageable);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all order items",
            description = "Retrieve all order items for a specific order")
    public List<OrderItemResponseDto> getAllOrderItems(@PathVariable Long orderId,
                                                       @ParameterObject Pageable pageable) {
        return orderService.getAllOrderItems(orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get order item",
            description = "Retrieve a specific order item within an order")
    public OrderItemResponseDto getOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderService.getOrderItem(orderId, itemId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status", description = "Update order status")
    public OrderResponseDto updateStatus(@PathVariable Long orderId,
                            @RequestBody UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateStatus(orderId, requestDto);
    }
}
