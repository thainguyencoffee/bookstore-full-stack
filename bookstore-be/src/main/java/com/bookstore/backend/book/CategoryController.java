package com.bookstore.backend.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/categories", produces = "application/json")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories(@PageableDefault(size = 12) Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}/books")
    public List<Book> getAllBooksByCategory(@PathVariable Long id, Pageable pageable) {
        return categoryService.findAllBooksByCategory(id, pageable);
    }
}
