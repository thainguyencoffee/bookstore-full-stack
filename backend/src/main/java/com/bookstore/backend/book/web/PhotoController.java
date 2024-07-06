package com.bookstore.backend.book.web;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.BookService;
import com.bookstore.backend.core.cloudinary.CloudinaryUtils;
import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("books")
public class PhotoController {

    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);
    private final Cloudinary cloudinary;
    private final BookService bookService;

    public PhotoController(Cloudinary cloudinary, BookService bookService) {
        this.cloudinary = cloudinary;
        this.bookService = bookService;
    }

    @PutMapping(value = "/{isbn}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book uploadPhoto(@PathVariable String isbn, @ModelAttribute List<MultipartFile> photos) {
        Book book = bookService.findByIsbn(isbn);
        var oldPhotos = book.getPhotos();
        // clear old photos
        if (oldPhotos!= null && !oldPhotos.isEmpty()) {
            book.getPhotos().forEach(photoUrl -> {
                try {
                    CloudinaryUtils.deleteFile(CloudinaryUtils.convertUrlToPublicId(photoUrl), cloudinary);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        List<String> urls = CloudinaryUtils.convertListMultipartFileToListUrl(photos, cloudinary);
        book.setPhotos(urls);
        return bookService.save(book);
    }

}
