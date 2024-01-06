package bookstore.dto.shoppingcart;

import bookstore.dto.cartitem.CartItemResponseDto;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
