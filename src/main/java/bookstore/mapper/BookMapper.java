package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.book.BookDto;
import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.model.Book;
import bookstore.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toEntity(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategoryIds(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .toList());
    }
}
