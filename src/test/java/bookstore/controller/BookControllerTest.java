package bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
public class BookControllerTest {
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
                    new ClassPathResource("database/add-three-books-to-books-table.sql")
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
                   new ClassPathResource("database/delete-books-from-books-table.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    public void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto().setIsbn("9882323");
        BookDto expected = createBookDto().setIsbn("9882323");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser
    @Test
    @DisplayName("Get all books")
    public void findAll_GivenListOfBooks_ShouldReturnAllBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
                .setId(1L)
                .setTitle("Harry Potter")
                .setAuthor("J Rowling")
                .setIsbn("123456-890")
                .setPrice(BigDecimal.valueOf(23.59))
                .setCategoryIds(List.of(1L)));
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Harry Potter 2")
                .setAuthor("J Rowling")
                .setIsbn("123456-899")
                .setPrice(BigDecimal.valueOf(25.59))
                .setCategoryIds(List.of(1L)));
        expected.add(new BookDto()
                .setId(3L)
                .setTitle("Kobzar")
                .setAuthor("T Shevchenko")
                .setIsbn("123456-999")
                .setPrice(BigDecimal.valueOf(27.59))
                .setCategoryIds(List.of(2L)));

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by id")
    public void findById_WithValidId_ShouldReturnValidBookDto() throws Exception {
        BookDto expected = createBookDto();
        Long bookId = expected.getId();
        MvcResult result = mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by wrong id")
    public void findById_WithNotValidId_ShouldReturnStatusNotFound() throws Exception {
        Long bookId = 150L;
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete book by Id")
    public void deleteById_WithValidId_ShouldReturnStatusNoContent() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by id")
    public void updateById_WithValidId_ShouldReturnUpdatedBookDto() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = createBookRequestDto()
                .setAuthor("J Tolkien").setTitle("Lord of the Rings");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto expected = createBookDto().setAuthor("J Tolkien").setTitle("Lord of the Rings");
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by wrong id")
    public void updateById_WithNotValidId_ShouldReturnStatusNotFound() throws Exception {
        Long bookId = 150L;
        CreateBookRequestDto requestDto = createBookRequestDto()
                .setAuthor("J Tolkien").setTitle("Lord of the Rings");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser
    @Test
    @DisplayName("Search book by author")
    public void search_WithValidAuthor_ShouldReturnValidListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "J Rowling"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("Harry Potter")
                .setAuthor("J Rowling")
                .setIsbn("123456-890")
                .setPrice(BigDecimal.valueOf(23.59))
                .setCategoryIds(List.of(1L)));
        expected.add(new BookDto().setId(2L)
                .setTitle("Harry Potter 2")
                .setAuthor("J Rowling")
                .setIsbn("123456-899")
                .setPrice(BigDecimal.valueOf(25.59))
                .setCategoryIds(List.of(1L)));
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser
    @Test
    @DisplayName("Search book by title")
    public void search_WithValidTitle_ShouldReturnValidListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("titles", "Kobzar"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
                .setId(3L)
                .setTitle("Kobzar")
                .setAuthor("T Shevchenko")
                .setIsbn("123456-999")
                .setPrice(BigDecimal.valueOf(27.59))
                .setCategoryIds(List.of(2L)));
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Harry Potter")
                .setAuthor("J Rowling")
                .setIsbn("123456-890")
                .setPrice(BigDecimal.valueOf(23.59))
                .setCategoryIds(List.of(1L));
    }

    private BookDto createBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("Harry Potter")
                .setAuthor("J Rowling")
                .setIsbn("123456-890")
                .setPrice(BigDecimal.valueOf(23.59))
                .setCategoryIds(List.of(1L));
    }
}
