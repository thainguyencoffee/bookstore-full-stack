package com.bookstore.backend.core.demo;

import com.bookstore.backend.book.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("datatest")
@Slf4j
@RequiredArgsConstructor
public class DataTest {

    private static final Random random = new Random();
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private static final List<String> thumbnailsCategory = List.of(
            "https://cdn0.fahasa.com/media/wysiwyg/Thang-06-2024/icon_ManngaT06.png",
            "https://cdn0.fahasa.com/media/wysiwyg/Duy-VHDT/Icon_Balo_120x120.png",
            "https://cdn0.fahasa.com/media/wysiwyg/Thang-08-2024/Icon_Bitex_t8.png",
            "https://cdn0.fahasa.com/media/wysiwyg/icon-menu/Icon_SanPhamMoi_8px_1.png"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void setupDataTest() {
        clear();

        var categoryList = new ArrayList<Category>();
        for (int i = 0; i < 5; i++) {
            Category category = new Category();
            category.setName(generateRandomName());
            if (i > 0) {
                category.setParent(categoryList.get(i - 1));
            }
            category.setThumbnail(thumbnailsCategory.get(random.nextInt(thumbnailsCategory.size())));
            categoryRepository.save(category);
            categoryList.add(category);
        }

        var booksList = new ArrayList<Book>();
        for (long isbn : generatedNumbers()) {
            Book book = buildExampleBook(isbn);
            book.setCategory(categoryList.get(random.nextInt(categoryList.size())));
            booksList.add(book);
        }

        bookRepository.saveAll(booksList);
    }

    private void clear() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Set<Long> generatedNumbers() {
        Set<Long> generatedNumbers = new HashSet<>();

        while (generatedNumbers.size() < 50) {
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            generatedNumbers.add(number);
        }
        return generatedNumbers;
    }

    private static Book buildExampleBook(Long isbn) {
        var book = new Book();
        book.setIsbn(String.valueOf(isbn));
        book.setTitle(generateRandomName());
        book.setAuthor(generateRandomName());
        book.setPublisher(generateRandomName());
        book.setSupplier(generateRandomName());
        book.setPrice(19000000L);
        book.setInventory(3);
        book.setLanguage(Language.ENGLISH);
        book.setCoverType(CoverType.HARDCOVER);
        book.setNumberOfPages(25);
        book.setMeasure(new Measure(120d, 180d, 10d, 200d));
        book.setPurchases(0);
        book.setThumbnails(List.of(
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216795/samples/ecommerce/accessories-bag.jpg",
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216795/samples/ecommerce/leather-bag-gray.jpg",
                "http://res.cloudinary.com/dl0v8gbku/image/upload/v1718216793/samples/landscapes/beach-boat.jpg"
        ));
        return book;
    }

    private static final String[] FIRST_NAME = {
            "Max", "Bella", "Charlie", "Lucy", "Cooper", "Daisy", "Buddy", "Luna",
            "Rocky", "Sadie", "Bailey", "Chloe", "Jack", "Lily", "Toby", "Zoe"
    };

    private static final String[] SECOND_NAME = {
            "Bear", "Duke", "Lucky", "Princess", "Shadow", "Coco", "Buster", "Molly",
            "Harley", "Ruby", "Tucker", "Penny", "Riley", "Maggie", "Bentley", "Sophie"
    };

    public static String generateRandomName() {
        boolean isDoubleName = random.nextBoolean();

        if (isDoubleName) {
            String firstName = FIRST_NAME[random.nextInt(FIRST_NAME.length)];
            String secondName = SECOND_NAME[random.nextInt(SECOND_NAME.length)];
            return firstName + " " + secondName;
        } else {
            return FIRST_NAME[random.nextInt(FIRST_NAME.length)];
        }
    }

}