package bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CreateCategoryRequestDto;
import bookstore.exception.EntityNotFoundException;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Category;
import bookstore.repository.category.CategoryRepository;
import bookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
          Verify findAll() method works
            """)
    public void findAll_ValidPageable_ReturnsAllCategories() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fantasy");
        category.setDescription("Awesome category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Fantasy");
        categoryDto.setDescription("Awesome category");

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        assertThat(categoryDtos).hasSize(1);
        assertThat(categoryDtos.get(0)).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
          Verify the correct  category was returned when ID exists  
            """)
    public void getCategory_WithValidCategoryId_ShouldReturnValidCategory() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fantasy");
        category.setDescription("Awesome category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryDto expected = new CategoryDto();
        expected.setId(categoryId);
        expected.setName("Fantasy");
        expected.setDescription("Awesome category");

        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(categoryId);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    @DisplayName("""
          Verify  if there is an exception if category is not found by Id 
            """)
    public void getCategory_WithNonExistingCategoryId_ShouldThrowException() {
        Long categoryId = 150L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        String expected = "Can't find category by id " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("""
          Verify the correct category was returned after saving
            """)
    public void saveCategory_WithValidRequestDto_ShouldReturnCorrectCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Awesome category");

        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fantasy");
        category.setDescription("Awesome category");

        CategoryDto expected = new CategoryDto();
        expected.setId(categoryId);
        expected.setName("Fantasy");
        expected.setDescription("Awesome category");

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
          Verify if category updated by Id
            """)
    public void updateById_WithValidCategoryId_ShouldReturnUpdatedCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Awesome category");

        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        CategoryDto expected = new CategoryDto();
        expected.setId(categoryId);
        expected.setName("Fantasy");
        expected.setDescription("Awesome category");

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(categoryId, requestDto);

        assertNotNull(actual);
        assertEquals(categoryId, actual.getId());
        assertEquals(requestDto.getName(), actual.getName());
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("""
          Verify if there is an exception if category has non existent Id
            """)
    public void updateById_WithNonExistentCategoryId_ShouldThrowException() {
        Long categoryId = 150L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fantasy");
        requestDto.setDescription("Awesome category");

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));

        String expected = "Can't update category by id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).existsById(categoryId);
    }

    @Test
    @DisplayName("""
          Verify if the delete method was called
            """)
    public void deleteById_WithValidCategoryId_ShouldCalled() {
        Long categoryId = 1L;
        categoryService.deleteById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
