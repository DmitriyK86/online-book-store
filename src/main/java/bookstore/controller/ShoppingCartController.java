package bookstore.controller;

import bookstore.dto.cartitem.CartItemAddRequestDto;
import bookstore.dto.cartitem.CartItemQuantityRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import bookstore.service.CartItemService;
import bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @Operation(summary = "Get shopping cart", description = "Get shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get shopping cart", description = "Get shopping cart")
    public void addBookToShoppingCart(Authentication authentication,
                                      @RequestBody @Valid CartItemAddRequestDto requestDto) {
        shoppingCartService.addItemToCart(authentication, requestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update quantity",
            description = "Update quantity of a book in the shopping cart")
    public void updateCartItemQuantity(Authentication authentication, @PathVariable Long cartItemId,
                                       @RequestBody @Valid CartItemQuantityRequestDto requestDto) {
        shoppingCartService.updateCartItemQuantity(authentication, cartItemId, requestDto);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item by id", description = "Delete cart item by id")
    public void delete(@PathVariable Long cartItemId) {
        cartItemService.delete(cartItemId);
    }
}
