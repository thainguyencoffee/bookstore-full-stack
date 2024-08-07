package com.bookstore.resourceserver.book.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MultipleThumbnailUpdateDto {

    @NotNull(message = "Thumbnails change must not be null")
    private Set<String> thumbnailsChange = new HashSet<>();
    @NotNull(message = "Thumbnails add must not be null")
    private List<MultipartFile> thumbnailsAdd = new ArrayList<>();

}
