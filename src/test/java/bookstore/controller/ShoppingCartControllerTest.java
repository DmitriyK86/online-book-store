package bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.dto.cartitem.CartItemResponseDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/add-data-for-shoppingcart-cartitem-tests.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/delete-data-for-shoppingcart-cartitem-tests.sql")
            );
        }
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Get shopping cart")
    public void getShoppingCart_GivenUser_ShouldReturnShoppingCart() throws Exception {
        ShoppingCartDto expected = createShoppingCartDto();
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "bob@gmail.com")
    @Test
    @DisplayName("Get shopping cart with non existent user")
    public void getShoppingCart_WithNotValidUser_ShouldReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Add book to shopping cart")
    public void addBookToShoppingCart_ValidRequestDto_Success() throws Exception {
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto().setBookId(1L).setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Add book to shopping cart with not valid request")
    public void addBookToShoppingCart_NotValidRequestDto_ShouldReturnStatusNotFound()
            throws Exception {
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto()
                .setBookId(100L)
                .setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Update cart item quantity")
    public void updateCartItemQuantity_ValidRequestDto_Success() throws Exception {
        Long cartItemId = 1L;
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto().setBookId(1L).setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cart/cart-items/{cartItemId}", cartItemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Update cart item quantity")
    public void updateCartItemQuantity_NotValidRequestDto_ShouldReturnStatusNotFound()
            throws Exception {
        Long cartItemId = 100L;
        CartItemAddRequestDto requestDto = new CartItemAddRequestDto()
                .setBookId(100L)
                .setQuantity(2);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cart/cart-items/{cartItemId}", cartItemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin@gmail.com")
    @Test
    @DisplayName("Delete cart item by Id")
    public void deleteById_WithValidId_ShouldReturnStatusNoContent() throws Exception {
        Long cartItemId = 1L;
        mockMvc.perform(delete("/cart/cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private ShoppingCartDto createShoppingCartDto() {
        CartItemResponseDto cartItemResponseDto = createCartItemResponseDto();
        return new ShoppingCartDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(Set.of(cartItemResponseDto));
    }

    private CartItemResponseDto createCartItemResponseDto() {
        return new CartItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Harry Potter")
                .setQuantity(2);
    }
}
