package com.bookstore.resourceserver.book.ebook.impl;

import com.bookstore.resourceserver.awss3.MultiMediaService;
import com.bookstore.resourceserver.book.BookService;
import com.bookstore.resourceserver.book.dto.book.BookRequestDto;
import com.bookstore.resourceserver.book.dto.book.BookUpdateDto;
import com.bookstore.resourceserver.book.dto.book.ebook.EBookRequestDto;
import com.bookstore.resourceserver.book.dto.book.ebook.EBookUpdateDto;
import com.bookstore.resourceserver.book.ebook.EBook;
import com.bookstore.resourceserver.book.ebook.EBookRepository;
import com.bookstore.resourceserver.book.impl.BookServiceImpl;
import com.bookstore.resourceserver.book.valuetype.BookProperties;
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
public class EBookServiceImpl implements BookService<EBook> {

    private final EBookRepository eBookRepository;
    private final BookServiceImpl bookServiceImpl;
    private final MultiMediaService multiMediaService;

    @Override
    public EBook findByIsbn(String isbn) {
        return eBookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new CustomNoResultException(EBook.class, ISBN, isbn));
    }

    @Override
    public Page<EBook> findAll(Pageable pageable) {
        return eBookRepository.findAll(pageable);
    }

    @Override
    public EBook save(Object dto) {
        if (dto instanceof EBookRequestDto eBookRequestDto) {
            bookServiceImpl.findByIsbn(eBookRequestDto.getIsbn());
            EBook eBook = convertToBook(eBookRequestDto);
            return eBookRepository.save(eBook);
        } else {
            throw new EntityCastException(dto.getClass(), BookRequestDto.class);
        }
    }

    @Override
    public EBook updateByIsbn(String isbn, Object dto) {
        if (dto instanceof EBookUpdateDto eBookUpdateDto) {
            EBook eBook = findByIsbn(isbn);
            BookProperties properties = eBook.getProperties();
            Price price = properties.getPrice();
            Optional.ofNullable(eBookUpdateDto.getOriginalPrice()).ifPresent(price::setOriginalPrice);
            Optional.ofNullable(eBookUpdateDto.getDiscountedPrice()).ifPresent(price::setDiscountedPrice);
            Optional.ofNullable(eBookUpdateDto.getCurrencyPrice()).ifPresent(price::setCurrencyPrice);
            properties.setPrice(price);
            if (eBookUpdateDto.getMetadata() != null) {
                if (eBook.getMetadata() != null) {
                    multiMediaService.deleteEverything(eBook.getMetadata().getUrl());
                }
                eBook.setMetadata(eBookUpdateDto.getMetadata());
            }
            Optional.ofNullable(eBookUpdateDto.getPublicationDate()).ifPresent(properties::setPublicationDate);
            Optional.ofNullable(eBookUpdateDto.getReleaseDate()).ifPresent(properties::setReleaseDate);
            return eBookRepository.save(eBook);
        } else {
            throw new EntityCastException(dto.getClass(), BookUpdateDto.class);
        }
    }

    @Override
    public void deleteByIsbn(String isbn) {
        eBookRepository.deleteByIsbn(isbn);
    }

    private static EBook convertToBook(EBookRequestDto dto) {
        var eBook = new EBook();
        eBook.setIsbn(dto.getIsbn());
        BookProperties properties = new BookProperties();
        properties.setPurchases(0);
        properties.setPrice(new Price(dto.getOriginalPrice(), dto.getDiscountedPrice(), dto.getCurrencyPrice()));
        properties.setNumberOfPages(dto.getNumberOfPages());
        properties.setReleaseDate(dto.getReleaseDate());
        properties.setPublicationDate(dto.getPublicationDate());
        eBook.setMetadata(dto.getMetadata());
        eBook.setProperties(properties);
        return eBook;
    }

}
