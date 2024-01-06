package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import bookstore.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("""
          Verify the correct shopping cart was returned by id
            """)
    public void getShoppingCart_WithValidId_ShouldReturnCorrectShoppingCartDto() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        ShoppingCart shoppingCart = createShoppingCart(user);
        ShoppingCartDto expected = createShoppingCartDto(shoppingCart);

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
    }

    @Test
    @DisplayName("""
          Verify  if there is an exception if user is not found by Id 
            """)
    public void getShoppingCart_WithNonExistingId_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCart(authentication));
        String expected = "Can't find shopping cart by id " + user.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("""
          Verify adding an item to the shopping cart
            """)
    public void addItemToCart_WithValidCartItem_ShouldAddItemToCart() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        Book bookToAdd = createBook();
        CartItem cartItem = createCartItem(bookToAdd);
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto();
        requestDto.setBookId(cartItem.getBook().getId());
        requestDto.setQuantity(cartItem.getQuantity());
        ShoppingCart shoppingCart = createShoppingCart(user);

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(bookToAdd));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(null);

        shoppingCartService.addItemToCart(authentication, requestDto);

        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verify(bookRepository, times(1)).findById(requestDto.getBookId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("""
          Verify adding an item to the shopping cart when item exists
            """)
    public void addItemToCart_WithValidExistingCartItem_ShouldAddItemToCart() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        Book bookToAdd = createBook();
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto();
        requestDto.setBookId(bookToAdd.getId());
        requestDto.setQuantity(10);
        ShoppingCart shoppingCart = mock(ShoppingCart.class);
        CartItem existingCartItem = createCartItem(bookToAdd);

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(bookToAdd));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(null);
        when(shoppingCart.getCartItems()).thenReturn(new HashSet<>(Set.of(existingCartItem)));

        shoppingCartService.addItemToCart(authentication, requestDto);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertEquals(15, existingCartItem.getQuantity());
    }

    @Test
    @DisplayName("""
          Verify if there is exception when adding non existing shopping cart
            """)
    public void addItemToCart_WithNonExistingShoppingCart_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto();

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(authentication, requestDto));
        String expected = "Can't find shopping cart by id " + user.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(bookRepository, cartItemRepository);
    }

    @Test
    @DisplayName("""
          Verify if there is exception when adding non existing cart item
            """)
    public void addItemToCart_WithNonExistingCartItem_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto();
        Long bookId = 100L;
        requestDto.setBookId(bookId);
        requestDto.setQuantity(10);
        ShoppingCart shoppingCart = createShoppingCart(user);

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(authentication, requestDto));
        String expected = "Can't find book with id " + bookId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verify(bookRepository, times(1)).findById(requestDto.getBookId());
    }

    @Test
    @DisplayName("""
          Verify updating item's quantity
            """)
    public void updateCartItemQuantity_WithValidCartItem_ShouldUpdateCartItemQuantity() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        CartItemQuantityRequestDto requestDto = new CartItemQuantityRequestDto();
        requestDto.setQuantity(10);
        Book bookToAdd = createBook();
        CartItem cartItem = createCartItem(bookToAdd);
        ShoppingCart shoppingCart = createShoppingCart(user);

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(),
                shoppingCart.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(null);

        shoppingCartService.updateCartItemQuantity(authentication, cartItem.getId(), requestDto);
        assertEquals(requestDto.getQuantity(), cartItem.getQuantity());

        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verify(cartItemRepository, times(1))
                .findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("""
          Verify if there is exception when updating item's quantity with non shopping cart
            """)
    public void updateCartItemQuantity_WithNonExistingShoppingCart_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        ShoppingCart shoppingCart = createShoppingCart(user);
        CartItemQuantityRequestDto requestDto = new CartItemQuantityRequestDto();
        requestDto.setQuantity(10);
        Long cartItemId = 1L;

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(authentication,
                        cartItemId, requestDto));
        String expected = "Can't find shopping cart by id " + user.getId();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
          Verify if there is exception when updating item's quantity with non existing Id
            """)
    public void updateCartItemQuantity_WithNonExistingCartItem_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        User user = createUser();
        ShoppingCart shoppingCart = createShoppingCart(user);
        CartItemQuantityRequestDto requestDto = new CartItemQuantityRequestDto();
        requestDto.setQuantity(10);
        Long cartItemId = 100L;

        when(userService.getUser(authentication)).thenReturn(user);
        when(shoppingCartRepository.findById(shoppingCart.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(authentication,
                        cartItemId, requestDto));
        String expected = "Can't find cart item by id" + cartItemId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(authentication);
        verify(shoppingCartRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
          Verify if clearShoppingCart() method works
            """)
    public void clearShoppingCart_WithValidShoppingCart_ShouldDeleteItemsFromCart() {
        Book bookToAdd = createBook();
        CartItem cartItem1 = createCartItem(bookToAdd);
        CartItem cartItem2 = createCartItem(bookToAdd);
        cartItem2.setId(2L);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);
        User user = createUser();
        ShoppingCart shoppingCart = createShoppingCart(user);
        shoppingCart.setCartItems(cartItems);

        shoppingCartService.clearShoppingCart(shoppingCart);

        verify(cartItemRepository, times(1)).deleteById(cartItem1.getId());
        verify(cartItemRepository, times(1)).deleteById(cartItem2.getId());
    }

    private ShoppingCart createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setDeleted(false);
        return shoppingCart;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");
        user.setPassword("123456789");
        user.setFirstName("Bob");
        user.setLastName("Bob");
        user.setShippingAddress("Some address");
        user.setDeleted(false);
        return user;
    }

    private ShoppingCartDto createShoppingCartDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCartDto.getUserId());
        return shoppingCartDto;
    }

    private Book createBook() {
        Book book = new Book();
        Long bookId = 1L;
        book.setId(bookId);
        book.setTitle("Harry Potter");
        book.setAuthor("J Rowling");
        book.setIsbn("12345-566");
        book.setPrice(new BigDecimal("23.33"));
        return book;
    }

    private CartItem createCartItem(Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(5);
        cartItem.setDeleted(false);
        return cartItem;
    }
}
