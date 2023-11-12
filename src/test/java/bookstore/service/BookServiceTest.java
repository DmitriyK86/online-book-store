package bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.book.BookSearchParameters;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.book.BookRepository;
import bookstore.repository.book.BookSpecificationBuilder;
import bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
          Verify the correct book was returned after saving
            """)
    public void saveBook_WithValidRequestDto_ShouldReturnCorrectBookDto() {
        CreateBookRequestDto requestDto = createBookRequestDto();
        Book book = createBook();

        BookDto expected = createBookDto(book);

        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.save(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
          Verify findAll() method works
            """)
    public void findAll_ValidPageable_ReturnsAllBooks() {
        Book book = createBook();
        BookDto bookDto = createBookDto(book);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> bookDtos = bookService.findAll(pageable);

        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.get(0)).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
          Verify the correct book was returned when ID exists  
            """)
    public void getBook_WithValidBookId_ShouldReturnValidBook() {
        Book book = createBook();
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookDto expected = createBookDto(book);

        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(bookId);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    @DisplayName("""
          Verify  if there is an exception if book is not found by Id 
            """)
    public void getBook_WithNonExistingBookId_ShouldThrowException() {
        Long bookId = 150L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId));

        String expected = "Can't find book by id " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("""
          Verify if the delete method was called
            """)
    public void deleteBookById_WithValidBookId_ShouldCalled() {
        Long bookId = 1L;
        bookService.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
          Verify if book updated by Id
            """)
    public void updateById_WithValidBookId_ShouldReturnUpdatedBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Harry Potter 2");
        requestDto.setAuthor("J Rowling");
        requestDto.setIsbn("12345-587");
        requestDto.setPrice(new BigDecimal("25.50"));

        Book book = new Book();
        Long bookId = 1L;
        book.setId(bookId);

        BookDto expected = new BookDto();
        expected.setId(bookId);
        expected.setTitle("Harry Potter 2");
        expected.setAuthor("J Rowling");
        expected.setIsbn("12345-587");
        expected.setPrice(new BigDecimal("25.50"));

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.updateById(bookId, requestDto);

        assertNotNull(actual);
        assertEquals(bookId, actual.getId());
        assertEquals(requestDto.getTitle(), actual.getTitle());
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookMapper, times(1)).toEntity(requestDto);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("""
          Verify if there is an exception if book has non existent Id
            """)
    public void updateById_WithNonExistentBookId_ShouldThrowException() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Harry Potter 2");
        requestDto.setAuthor("J Rowling");
        requestDto.setIsbn("12345-587");
        requestDto.setPrice(new BigDecimal("25.50"));
        Long bookId = 150L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(bookId, requestDto));

        String expected = "Can't update book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).existsById(bookId);
    }

    @Test
    @DisplayName("""
          Verify if search() method works
            """)
    public void search_WithValidParams_ShouldReturnCorrectBookDtos() {
        Book book = createBook();
        Long bookId = 1L;

        BookDto bookDto = createBookDto(book);
        bookDto.setDescription("Awesome book");

        String[] titles = new String[]{"Harry Potter"};
        String[] authors = new String[]{"J Rowling"};
        String[] isbns = new String[]{"12345-566"};
        String[] prices = new String[]{"26.33"};
        String[] descriptions = new String[]{};
        BookSearchParameters params = new BookSearchParameters(titles, authors, isbns, prices,
                descriptions);
        Specification<Book> bookSpecification = mock(Specification.class);
        List<Book> books = List.of(book);
        List<BookDto> expectedDtos = List.of(bookDto);

        when(bookSpecificationBuilder.build(params)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification)).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDtos.get(0));

        List<BookDto> result = bookService.search(params);

        assertEquals(expectedDtos, result);

        verify(bookSpecificationBuilder, times(1)).build(params);
        verify(bookRepository, times(1)).findAll(bookSpecification);
        verify(bookMapper, times(books.size())).toDto(any(Book.class));
    }

    @Test
    @DisplayName("""
          Verify the correct list of books was returned by category Id
            """)
    public void findAllByCategoryId_WithValidCategoryId_ShouldReturnValidListOfBooks() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fantasy");
        category.setDescription("Awesome category");

        Book book1 = createBook();
        book1.setDescription("Awesome book");
        book1.setCategories(Set.of(category));

        Book book2 = new Book();
        Long bookId2 = 2L;
        book2.setId(bookId2);
        book2.setTitle("Harry Potter 2");
        book2.setAuthor("J Rowling");
        book2.setIsbn("12345-588");
        book2.setPrice(new BigDecimal("25.33"));
        book2.setDescription("Awesome book");
        book2.setCategories(Set.of(category));

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds1 = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds1.setId(book1.getId());
        bookDtoWithoutCategoryIds1.setTitle("Harry Potter");
        bookDtoWithoutCategoryIds1.setAuthor("J Rowling");
        bookDtoWithoutCategoryIds1.setIsbn("12345-566");
        bookDtoWithoutCategoryIds1.setPrice(new BigDecimal("23.33"));
        bookDtoWithoutCategoryIds1.setDescription("Awesome book");

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds2 = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds2.setId(bookId2);
        bookDtoWithoutCategoryIds2.setTitle("Harry Potter 2");
        bookDtoWithoutCategoryIds2.setAuthor("J Rowling");
        bookDtoWithoutCategoryIds2.setIsbn("12345-588");
        bookDtoWithoutCategoryIds2.setPrice(new BigDecimal("25.33"));
        bookDtoWithoutCategoryIds2.setDescription("Awesome book");

        List<Book> books = List.of(book1, book2);

        when(bookRepository.findAllByCategoryId(categoryId)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategoryIds(book1)).thenReturn(bookDtoWithoutCategoryIds1);
        when(bookMapper.toDtoWithoutCategoryIds(book2)).thenReturn(bookDtoWithoutCategoryIds2);

        List<BookDtoWithoutCategoryIds> expectedDtos = List.of(bookDtoWithoutCategoryIds1,
                bookDtoWithoutCategoryIds2);
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(categoryId);

        assertEquals(expectedDtos, actual);
        verify(bookRepository, times(1)).findAllByCategoryId(categoryId);
        verify(bookMapper, times(books.size())).toDtoWithoutCategoryIds(any(Book.class));
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Harry Potter");
        requestDto.setAuthor("J Rowling");
        requestDto.setIsbn("12345-566");
        requestDto.setPrice(new BigDecimal("23.33"));
        return requestDto;
    }

    private Book createBook() {
        Book book = new Book();
        Long bookId = 1L;
        book.setId(bookId);
        book.setTitle("Harry Potter");
        book.setAuthor("J Rowling");
        book.setIsbn("12345-566");
        book.setPrice(new BigDecimal("23.33"));
        return book;
    }

    private BookDto createBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        return bookDto;
    }
}

