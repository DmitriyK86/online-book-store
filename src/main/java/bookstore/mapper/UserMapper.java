package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.user.UserRegistrationRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
