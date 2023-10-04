package bookstore.service.impl;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.dto.cartitem.CartItemQuantityRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.book.BookRepository;
import bookstore.repository.cartitem.CartItemRepository;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import bookstore.service.ShoppingCartService;
import bookstore.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserService userService;

    @Override
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = userService.getUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart by id " + user.getId()));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void addItemToCart(Authentication authentication, CartItemAddRequestDto requestDto) {
        User user = userService.getUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart by id " + user.getId()));
        Book bookToAdd = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id "
                        + requestDto.getBookId()));
        Optional<CartItem> cartItemFromDB = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();
        CartItem cartItemToUpdate = new CartItem();
        if (cartItemFromDB.isPresent()) {
            cartItemToUpdate = cartItemFromDB.get();
            cartItemToUpdate.setQuantity(cartItemToUpdate.getQuantity() + requestDto.getQuantity());
        } else {
            cartItemToUpdate.setShoppingCart(shoppingCart);
            cartItemToUpdate.setBook(bookToAdd);
            cartItemToUpdate.setQuantity(requestDto.getQuantity());
        }
        cartItemRepository.save(cartItemToUpdate);
    }

    @Override
    public void updateCartItemQuantity(Authentication authentication, Long cartItemId,
                                       CartItemQuantityRequestDto requestDto) {
        User user = userService.getUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find shopping cart by id " + user.getId()));
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item by id"
                        + cartItemId));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
    }

}
