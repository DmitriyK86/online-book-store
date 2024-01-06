package bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bookstore.model.ShoppingCart;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
          Find shopping cart by valid Id
            """)
    @Sql(scripts = "classpath:database/add-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_WithValidId_ReturnsValidShoppingCart() {
        Long shoppingCartId = 1L;
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(shoppingCartId);
        assertNotNull(shoppingCart);
        assertEquals(shoppingCartId, shoppingCart.get().getId());
    }

    @Test
    @DisplayName("""
          Find shopping cart by wrong Id
            """)
    @Sql(scripts = "classpath:database/add-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-data-for-shoppingcart-cartitem-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_WithWrongId_ReturnsOptionalEmpty() {
        Long shoppingCartId = 100L;
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(shoppingCartId);
        assertTrue(shoppingCart.isEmpty());
    }
}
