package bookstore.service;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.dto.cartitem.CartItemQuantityRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(Authentication authentication);

    void addItemToCart(Authentication authentication, CartItemAddRequestDto requestDto0);

    void updateCartItemQuantity(Authentication authentication, Long cartItemId,
                                CartItemQuantityRequestDto requestDto);
}
