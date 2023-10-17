package bookstore.service.impl;

import bookstore.dto.order.OrderResponseDto;
import bookstore.dto.order.PlaceOrderRequestDto;
import bookstore.dto.order.UpdateOrderStatusRequestDto;
import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.OrderItemMapper;
import bookstore.mapper.OrderMapper;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.order.OrderRepository;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import bookstore.service.OrderItemService;
import bookstore.service.OrderService;
import bookstore.service.ShoppingCartService;
import bookstore.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public void placeOrder(Authentication authentication, PlaceOrderRequestDto requestDto) {
        User user = userService.getUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart by id " + user.getId()));

        Order order = orderMapper.toEntity(shoppingCart);
        BigDecimal total = shoppingCart.getCartItems().stream()
                        .map(cartItem -> cartItem.getBook().getPrice()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        Order savedOrder = orderRepository.save(order);

        Set<OrderItem> orderItems = getOrderItemsFromCart(shoppingCart);
        orderItems.forEach(orderItem -> orderItem.setOrder(savedOrder));
        orderItems.forEach(orderItemService::save);
        order.setOrderItems(orderItems);

        shoppingCartService.clearShoppingCart(shoppingCart);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCartRepository.save(shoppingCart);
    }

    public List<OrderResponseDto> getOrdersHistory(Authentication authentication,
                                                   Pageable pageable) {
        User user = userService.getUser(authentication);
        return orderRepository.findAllByUserId(user.getId(), pageable).stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    public List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    public OrderItemResponseDto getOrderItem(Long orderId, Long itemId) {
        return orderItemService.findByOrderIdAndId(orderId, itemId);
    }

    @Override
    public OrderResponseDto updateStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by id " + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    private Set<OrderItem> getOrderItemsFromCart(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> orderItemMapper.toEntityFromCartItem(cartItem, cartItem.getBook()))
                .collect(Collectors.toSet());
    }
}
