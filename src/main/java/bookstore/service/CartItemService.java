package bookstore.service;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.model.CartItem;

public interface CartItemService {
    CartItem save(CartItemAddRequestDto requestDto);

    void delete(Long cartItemId);
}
