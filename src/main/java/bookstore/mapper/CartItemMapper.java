package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.dto.cartitem.CartItemResponseDto;
import bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "book.id", source = "bookId")
    CartItem toEntity(CartItemAddRequestDto requestDto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    public CartItemResponseDto toResponseDto(CartItem cartItem);
}
