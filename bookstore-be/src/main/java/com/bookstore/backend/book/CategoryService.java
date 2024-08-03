package com.bookstore.backend.book;

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

    public List<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public List<Book> findAllBooksByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomNoResultException(Category.class, CustomNoResultException.Identifier.ID, categoryId));
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        return bookRepository.findAllByCategoryId(category.getId(), limit, offset);
    }
}
