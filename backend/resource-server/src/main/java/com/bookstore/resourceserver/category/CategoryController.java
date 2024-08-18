package com.bookstore.resourceserver.category;

import com.bookstore.resourceserver.awss3.MultiMediaService;
import com.bookstore.resourceserver.book.Book;
import com.bookstore.resourceserver.category.dto.CategoryMetadataRequestDto;
import com.bookstore.resourceserver.category.dto.CategoryMetadataUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "categories", produces = "application/json")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final MultiMediaService multiMediaService;

    @Operation(summary = "Get all categories")
    @GetMapping
    public List<Category> getCategories(@PageableDefault(size = 12) Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(summary = "Get a category by id")
    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @Operation(summary = "Get all children of a category")
    @GetMapping("/{id}/children")
    public List<Category> getAllChildren(@PathVariable Long id) {
        return categoryService.findAllChildren(id);
    }

    @Operation(summary = "Get all books of a category")
    @GetMapping("/{id}/books")
    public List<Book> getAllBooksByCategory(@PathVariable Long id, Pageable pageable) {
        return categoryService.findAllBooksByCategory(id, pageable);
    }

    @Operation(summary = "Create a category", security = @SecurityRequirement(name = "token"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@Valid @RequestBody CategoryMetadataRequestDto categoryRequestDto) {
        return categoryService.save(categoryRequestDto);
    }

    @Operation(summary = "Update a category", security = @SecurityRequirement(name = "token"))
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryMetadataUpdateDto categoryUpdateDto) {
        return categoryService.updateById(id, categoryUpdateDto);
    }

    @Operation(summary = "Delete a category", security = @SecurityRequirement(name = "token"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Upload thumbnail for a category", security = @SecurityRequirement(name = "token"))
    @PostMapping(value = "/{id}/thumbnails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String uploadThumbnail(@PathVariable String id, @RequestPart MultipartFile thumbnail) {
        return multiMediaService.uploadEverything(Category.class, id, List.of(thumbnail), "thumbnail")
                .get(0);
    }

}
