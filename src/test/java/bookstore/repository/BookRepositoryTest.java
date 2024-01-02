package bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bookstore.model.Book;
import bookstore.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
          Find all books by valid category Id
            """)
    @Sql(scripts = "classpath:database/add-one-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_WithValidId_ReturnsOneBook() {
        Long categoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoryId(categoryId);
        assertEquals(1, actual.size());
        assertEquals("Harry Potter", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("""
          Find all books by valid category Id
            """)
    @Sql(scripts = "classpath:database/add-three-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_WithValidId_ReturnsTwoBooks() {
        Long categoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoryId(categoryId);
        assertEquals(2, actual.size());
        assertEquals("Harry Potter", actual.get(0).getTitle());
    }
}
