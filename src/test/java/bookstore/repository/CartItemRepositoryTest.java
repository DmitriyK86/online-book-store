package bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bookstore.model.CartItem;
import bookstore.repository.cartitem.CartItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("""
          Find cart item by valid Id and shopping cart Id
            """)
    @Sql(scripts = "classpath:database/add-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_WithValidId_ReturnsValidCartItem() {
        Long cartItemId = 1L;
        Long shoppingCartId = 1L;
        Optional<CartItem> cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCartId);
        assertNotNull(cartItem);
        assertEquals(cartItemId, cartItem.get().getId());
    }

    @Test
    @DisplayName("""
          Find cart item by wrong Id
            """)
    @Sql(scripts = "classpath:database/add-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_WithWrongId_ReturnsOptionalEmpty() {
        Long cartItemId = 100L;
        Long shoppingCartId = 1L;
        Optional<CartItem> cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCartId);
        assertTrue(cartItem.isEmpty());
    }
}
