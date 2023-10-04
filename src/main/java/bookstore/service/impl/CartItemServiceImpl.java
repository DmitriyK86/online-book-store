package bookstore.service.impl;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CartItemMapper;
import bookstore.model.CartItem;
import bookstore.repository.cartitem.CartItemRepository;
import bookstore.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItem save(CartItemAddRequestDto requestDto) {
        return cartItemRepository.save(cartItemMapper.toEntity(requestDto));
    }

    @Override
    public void delete(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new EntityNotFoundException("Can't find cart item with id " + cartItemId);
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
