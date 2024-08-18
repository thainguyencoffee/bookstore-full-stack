package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.awss3.MultiMediaService;
import com.bookstore.resourceserver.author.Author;
import com.bookstore.resourceserver.author.AuthorService;
import com.bookstore.resourceserver.book.dto.*;
import com.bookstore.resourceserver.book.dto.view.BookSalesView;
import com.bookstore.resourceserver.book.valuetype.CoverType;
import com.bookstore.resourceserver.book.valuetype.Language;
import com.bookstore.resourceserver.category.Category;
import com.bookstore.resourceserver.category.CategoryService;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final MultiMediaService multiMediaService;

    
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new CustomNoResultException(
                                Book.class,
                                CustomNoResultException.Identifier.ISBN, isbn));
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    
    public Book save(BookRequestDto bookRequestDto) {
        Book book = bookRequestDto.buildBook();
        Category category = categoryService.findById(bookRequestDto.category());
        book.setCategory(category);
        for (Long authorId : bookRequestDto.authorIds()) {
            Author author = authorService.findById(authorId);
            book.addAuthor(author);
        }
        return bookRepository.save(book);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public Book updateBookByIsbn(String isbn, BookUpdateDto bookUpdateDto) {
        var book = findByIsbn(isbn);
        Optional.ofNullable(bookUpdateDto.category()).ifPresent(categoryId -> book.setCategory(categoryService.findById(categoryId)));
        Optional.ofNullable(bookUpdateDto.title()).filter(title -> !title.isBlank()).ifPresent(book::setTitle);
        Optional.ofNullable(bookUpdateDto.publisher()).filter(publisher -> !publisher.isBlank()).ifPresent(book::setPublisher);
        Optional.ofNullable(bookUpdateDto.supplier()).filter(supplier -> !supplier.isBlank()).ifPresent(book::setSupplier);
        Optional.ofNullable(bookUpdateDto.description()).ifPresent(book::setDescription);
        Optional.ofNullable(bookUpdateDto.language()).filter(language -> !language.isBlank()).ifPresent(language -> book.setLanguage(Language.valueOf(language)));
        Optional.ofNullable(bookUpdateDto.edition()).ifPresent(book::setEdition);
        if (bookUpdateDto.authorIds() != null) {
            Set<Author> authorSet = new HashSet<>();
            for (Long authorId : bookUpdateDto.authorIds()) {
                Author author = authorService.findById(authorId);
                authorSet.add(author);
            }
            book.addAllAuthors(authorSet);
        }

        if (bookUpdateDto.thumbnails() != null) {
            book.setThumbnails(updateThumbnails(book.getThumbnails(), bookUpdateDto.thumbnails()));
        }
        return bookRepository.save(book);
    }

    private List<String> updateThumbnails(List<String> currentThumbnails, List<String> newThumbnails) {
        if (!currentThumbnails.isEmpty()) {
            currentThumbnails.removeAll(newThumbnails);
            multiMediaService.deleteEverything(currentThumbnails);
        }
        return newThumbnails;
    }


    public void deleteByIsbn(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Book saveEBook(String isbn, EBookRequestDto eBookRequestDto) {
        Book book = findByIsbn(isbn);
        EBook eBook = eBookRequestDto.buildEBook();
        book.setEBook(eBook);
        return bookRepository.save(book);
    }


    public Book updateEBookByIsbn(String isbn, EBookUpdateDto eBookUpdateDto) {
        Book book = findByIsbn(isbn);
        var eBook = book.getEBook();
        if (eBook == null) {
            return saveEBook(isbn, eBookUpdateDto.toEBookRequestDto());
        }
        Optional.ofNullable(eBookUpdateDto.numberOfPages()).ifPresent(pages -> eBook.getProperties().setNumberOfPages(pages));
        Optional.ofNullable(eBookUpdateDto.originalPrice()).ifPresent(op -> eBook.getProperties().getPrice().setOriginalPrice(op));
        Optional.ofNullable(eBookUpdateDto.discountedPrice()).ifPresent(dp -> eBook.getProperties().getPrice().setDiscountedPrice(dp));
        Optional.ofNullable(eBookUpdateDto.publicationDate()).ifPresent(publicationDate -> eBook.getProperties().setPublicationDate(publicationDate));
        Optional.ofNullable(eBookUpdateDto.releaseDate()).ifPresent(releaseDate -> eBook.getProperties().setReleaseDate(releaseDate));
        return bookRepository.save(book);
    }

    public void deleteEBookByIsbnAndId(String isbn) {
        var book = findByIsbn(isbn);
        book.setEBook(null);
        bookRepository.save(book);
    }

    public Book savePrintBook(String isbn, PrintBookRequestDto printBookRequestDto) {
        Book book = findByIsbn(isbn);
        PrintBook printBook = printBookRequestDto.buildPrintBook();
        book.setPrintBook(printBook);
        return bookRepository.save(book);
    }

    public Book updatePrintBookByIsbnAndId(String isbn, PrintBookUpdateDto printBookUpdateDto) {
        Book book = findByIsbn(isbn);
        var printBook = book.getPrintBook();
        if (printBook == null) {
            return savePrintBook(isbn, printBookUpdateDto.toPrintBookRequestDto());
        }
        Optional.ofNullable(printBookUpdateDto.coverType()).filter(coverType -> !coverType.isBlank()).ifPresent(coverType -> printBook.setCoverType(CoverType.valueOf(coverType)));
        Optional.ofNullable(printBookUpdateDto.inventory()).ifPresent(printBook::setInventory);
        Optional.ofNullable(printBookUpdateDto.numberOfPages()).ifPresent(pages -> printBook.getProperties().setNumberOfPages(pages));
        Optional.ofNullable(printBookUpdateDto.originalPrice()).ifPresent(originalPrice -> printBook.getProperties().getPrice().setOriginalPrice(originalPrice));
        Optional.ofNullable(printBookUpdateDto.discountedPrice()).ifPresent(discountedPrice -> printBook.getProperties().getPrice().setDiscountedPrice(discountedPrice));
        Optional.ofNullable(printBookUpdateDto.publicationDate()).ifPresent(publicationDate -> printBook.getProperties().setPublicationDate(publicationDate));
        Optional.ofNullable(printBookUpdateDto.releaseDate()).ifPresent(releaseDate -> printBook.getProperties().setReleaseDate(releaseDate));
        Optional.ofNullable(printBookUpdateDto.width()).ifPresent(width -> printBook.getMeasure().setWidth(width));
        Optional.ofNullable(printBookUpdateDto.height()).ifPresent(height -> printBook.getMeasure().setHeight(height));
        Optional.ofNullable(printBookUpdateDto.thickness()).ifPresent(thickness -> printBook.getMeasure().setThickness(thickness));
        Optional.ofNullable(printBookUpdateDto.weight()).ifPresent(weight -> printBook.getMeasure().setWeight(weight));
        return bookRepository.save(book);
    }

    public void deletePrintBookByIsbnAndId(String isbn) {
        var book = findByIsbn(isbn);
        book.setPrintBook(null);
        bookRepository.save(book);
    }

    public List<BookSalesView> findBestSellers(Integer top) {
        return bookRepository.getTopBookSales(top);
    }
}
