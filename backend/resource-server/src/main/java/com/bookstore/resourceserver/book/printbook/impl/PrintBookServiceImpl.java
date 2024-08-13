package com.bookstore.resourceserver.book.printbook.impl;

import com.bookstore.resourceserver.book.BookService;
import com.bookstore.resourceserver.book.dto.book.printbook.PrintBookRequestDto;
import com.bookstore.resourceserver.book.dto.book.printbook.PrintBookUpdateDto;
import com.bookstore.resourceserver.book.impl.BookServiceImpl;
import com.bookstore.resourceserver.book.printbook.PrintBook;
import com.bookstore.resourceserver.book.printbook.PrintBookRepository;
import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.book.valuetype.Measure;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import com.bookstore.resourceserver.core.exception.EntityCastException;
import com.bookstore.resourceserver.core.valuetype.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bookstore.resourceserver.core.exception.CustomNoResultException.Identifier.ISBN;

@Service
@RequiredArgsConstructor
public class PrintBookServiceImpl implements BookService<PrintBook> {

    private final PrintBookRepository printBookRepository;
    private final BookServiceImpl bookServiceImpl;

    @Override
    public PrintBook findByIsbn(String isbn) {
        return printBookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new CustomNoResultException(PrintBook.class, ISBN, isbn));
    }

    @Override
    public Page<PrintBook> findAll(Pageable pageable) {
        return printBookRepository.findAll(pageable);
    }

    @Override
    public PrintBook save(Object dto) {
        if (dto instanceof PrintBookRequestDto printBookRequestDto) {
            bookServiceImpl.findByIsbn(printBookRequestDto.getIsbn());
            var printBook = convertToBook(printBookRequestDto);
            return printBookRepository.save(printBook);
        } else {
            throw new EntityCastException(dto.getClass(), PrintBookRequestDto.class);
        }
    }

    @Override
    public PrintBook updateByIsbn(String isbn, Object dto) {
        if (dto instanceof PrintBookUpdateDto printBookUpdateDto) {
            PrintBook printBook = findByIsbn(isbn);
            BookProperties properties = printBook.getProperties();
            Price price = properties.getPrice();
            Optional.ofNullable(printBookUpdateDto.getOriginalPrice()).ifPresent(price::setOriginalPrice);
            Optional.ofNullable(printBookUpdateDto.getDiscountedPrice()).ifPresent(price::setDiscountedPrice);
            Optional.ofNullable(printBookUpdateDto.getCurrencyPrice()).ifPresent(price::setCurrencyPrice);
            properties.setPrice(price);
            Optional.ofNullable(printBookUpdateDto.getPublicationDate()).ifPresent(properties::setPublicationDate);
            Optional.ofNullable(printBookUpdateDto.getReleaseDate()).ifPresent(properties::setReleaseDate);
            Optional.ofNullable(printBookUpdateDto.getCoverType()).ifPresent(printBook::setCoverType);
            Measure measure = printBook.getMeasure();
            Optional.ofNullable(printBookUpdateDto.getWidth()).ifPresent(measure::setWidth);
            Optional.ofNullable(printBookUpdateDto.getHeight()).ifPresent(measure::setHeight);
            Optional.ofNullable(printBookUpdateDto.getThickness()).ifPresent(measure::setThickness);
            Optional.ofNullable(printBookUpdateDto.getWeight()).ifPresent(measure::setWeight);
            printBook.setMeasure(measure);
            Optional.ofNullable(printBookUpdateDto.getInventory()).ifPresent(printBook::setInventory);
            return printBookRepository.save(printBook);
        } else {
            throw new EntityCastException(dto.getClass(), PrintBookRequestDto.class);
        }
    }

    @Override
    public void deleteByIsbn(String isbn) {
        printBookRepository.deleteByIsbn(isbn);
    }

    private static PrintBook convertToBook(PrintBookRequestDto dto) {
        var printBook = new PrintBook();
        printBook.setIsbn(dto.getIsbn());
        BookProperties properties = new BookProperties();
        properties.setPurchases(0);
        properties.setPrice(new Price(dto.getOriginalPrice(), dto.getDiscountedPrice(), dto.getCurrencyPrice()));
        properties.setNumberOfPages(dto.getNumberOfPages());
        properties.setReleaseDate(dto.getReleaseDate());
        properties.setPublicationDate(dto.getPublicationDate());
        printBook.setProperties(properties);
        printBook.setCoverType(dto.getCoverType());
        printBook.setMeasure(new Measure(dto.getWidth(), dto.getHeight(), dto.getThickness(), dto.getWeight()));
        printBook.setInventory(dto.getInventory());
        return printBook;
    }

}
