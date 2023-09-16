package bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        @Size(min = 4, max = 50)
        String email,

        String password
) {
}
