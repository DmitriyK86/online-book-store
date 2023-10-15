package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.orderitem.OrderItemResponseDto;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "price", source = "book.price")
    OrderItem toEntityFromCartItem(CartItem cartItem, Book book);
}
