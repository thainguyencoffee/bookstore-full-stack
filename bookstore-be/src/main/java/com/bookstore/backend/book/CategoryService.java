package com.bookstore.backend.book;

import com.bookstore.backend.awss3.MultiMediaService;
import com.bookstore.backend.book.dto.category.CategoryMetadataRequestDto;
import com.bookstore.backend.book.dto.category.CategoryMetadataUpdateDto;
import com.bookstore.backend.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MultiMediaService multiMediaService;

    public List<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new CustomNoResultException(Category.class, CustomNoResultException.Identifier.ID, id));
    }

    public List<Book> findAllBooksByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CustomNoResultException(Category.class, CustomNoResultException.Identifier.ID, categoryId));
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        return bookRepository.findAllByCategoryId(category.getId(), limit, offset);
    }

    public Category save(CategoryMetadataRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        if (categoryRequestDto.getParentId() != null) {
            Category parent = findById(categoryRequestDto.getParentId());
            category.setParent(parent);
        }
        if (categoryRequestDto.getThumbnail() != null) {
            category.setThumbnail(categoryRequestDto.getThumbnail());
        }
        return categoryRepository.save(category);
    }

    public List<Category> findAllChildren(Long id) {
        return categoryRepository.findAllSubCategoriesById(id);
    }

    public Category updateById(Long id, CategoryMetadataUpdateDto categoryUpdateDto) {
        Category category = findById(id);
        if (categoryUpdateDto.getName() != null) {
            category.setName(categoryUpdateDto.getName());
        }
        if (categoryUpdateDto.getParentId() != null) {
            Category parent = findById(categoryUpdateDto.getParentId());
            category.setParent(parent);
        }
        if (categoryUpdateDto.getThumbnail() != null) {
            if (category.getThumbnail() != null) {
                multiMediaService.deleteEverything(category.getThumbnail());
            }
            category.setThumbnail(categoryUpdateDto.getThumbnail());
        }
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }

}
