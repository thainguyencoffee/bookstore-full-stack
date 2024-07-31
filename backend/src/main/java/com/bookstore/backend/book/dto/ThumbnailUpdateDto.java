package com.bookstore.backend.book.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ThumbnailUpdateDto {

    @NotNull(message = "thumbnailsChange must not be null")
    private Set<String> thumbnailsChange;
    @NotNull(message = "thumbnailsDelete must not be null")
    private List<MultipartFile> thumbnailsAdd;

    public void addThumbnail(String thumbnail) {
        thumbnailsChange.add(thumbnail);
    }
}
