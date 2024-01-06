package bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemAddRequestDto {
    @NotNull
    @Positive
    private Long bookId;
    @NotNull
    @Positive
    private int quantity;
}
