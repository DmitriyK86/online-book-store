package bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemQuantityRequestDto {
    @NotNull
    @Positive
    private int quantity;
}
