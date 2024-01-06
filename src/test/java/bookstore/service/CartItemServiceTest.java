package bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CartItemMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.repository.cartitem.CartItemRepository;
import bookstore.service.impl.CartItemServiceImpl;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Test
    @DisplayName("""
          Verify the correct cart item was returned after saving
            """)
    public void saveCartItem_WithValidRequestDto_ShouldReturnCorrectCartItem() {
        Book book = new Book();
        Long bookId = 1L;
        book.setId(bookId);
        book.setTitle("Harry Potter");
        book.setAuthor("J Rowling");
        book.setIsbn("12345-566");
        book.setPrice(new BigDecimal("23.33"));

        CartItemAddRequestDto requestDto = new CartItemAddRequestDto();
        requestDto.setBookId(book.getId());
        requestDto.setQuantity(5);

        CartItem expectedCartItem = new CartItem();
        expectedCartItem.setId(1L);
        expectedCartItem.setBook(book);
        expectedCartItem.setQuantity(5);

        when(cartItemMapper.toEntity(requestDto)).thenReturn(expectedCartItem);
        when(cartItemRepository.save(expectedCartItem)).thenReturn(expectedCartItem);

        CartItem actualCartItem = cartItemService.save(requestDto);

        assertNotNull(actualCartItem);
        assertEquals(expectedCartItem, actualCartItem);
        verify(cartItemMapper, times(1)).toEntity(requestDto);
        verify(cartItemRepository, times(1)).save(expectedCartItem);
    }

    @Test
    @DisplayName("""
          Verify if the delete method was called
            """)
    public void deleteCartItemById_WithValidId_ShouldCalled() {
        Long cartItemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(5);

        when(cartItemRepository.existsById(cartItemId)).thenReturn(true);

        cartItemService.delete(cartItemId);

        verify(cartItemRepository, times(1)).deleteById(cartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
          Verify if there is an exception if cart item has non existent Id
            """)
    public void deleteCartItemById_WithNonExistentId_ShouldThrowException() {
        Long cartItemId = 1L;

        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.delete(cartItemId));

        String expected = "Can't find cart item with id " + cartItemId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).existsById(cartItemId);
    }
}
