package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.order.OrderResponseDto;
import bookstore.model.Order;
import bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    Order toEntity(ShoppingCart shoppingCart);

    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toResponseDto(Order order);
}
