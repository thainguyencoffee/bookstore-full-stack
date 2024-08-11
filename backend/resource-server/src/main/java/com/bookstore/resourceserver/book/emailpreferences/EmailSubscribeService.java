package com.bookstore.resourceserver.book.emailpreferences;

import com.bookstore.resourceserver.book.Book;
import com.bookstore.resourceserver.book.BookService;
import com.bookstore.resourceserver.book.Category;
import com.bookstore.resourceserver.book.CategoryService;
import com.bookstore.resourceserver.core.email.EmailService;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bookstore.resourceserver.core.exception.CustomNoResultException.Identifier.EMAIL;

@Service
@RequiredArgsConstructor
public class EmailSubscribeService {

    private final CategoryService categoryService;
    private final BookService bookService;
    private final EmailPreferencesRepository emailPreferencesRepository;
    private final EmailService emailService;


    public Optional<EmailPreferences> findByEmail(String email) {
        return emailPreferencesRepository.findByEmail(email);
    }

    public EmailPreferences subscribe(EmailPreferencesRequestDto dto) {
        Optional<EmailPreferences> emailPreferencesOptional = findByEmail(dto.getEmail());
        EmailPreferences emailPreferences;
        if (emailPreferencesOptional.isPresent()) {
            return updateByEmail(emailPreferencesOptional.get(), dto);
        } else {
            emailPreferences = dto.toEmailPreferences();
            if (dto.getCategoryIds() != null) {
                Set<Category> categories = dto.getCategoryIds().stream().map(categoryService::findById).collect(Collectors.toSet());
                emailPreferences.addAllCategories(categories);
            }

            emailPreferences = emailPreferencesRepository.save(emailPreferences);
        }

        if (emailPreferences.getEmailTopics().contains(EmailTopic.DEAL_OF_THE_DAY)) {
            Instant firstDayOfPrevMonth = LocalDate.now()
                    .withDayOfMonth(1)
                    .minusMonths(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();
            Page<Book> booksPage = bookService.findBestSellers(firstDayOfPrevMonth,
                    PageRequest.of(0 , 10));
            // hasn't implemented discount yet
            emailService.sendConfirmationEmail(emailPreferences.getEmail(), "Welcome to Bookstore!",
                    emailService.buildPromotionalDealOfTheDayEmailBody(booksPage.getContent(), Collections.emptyList()));
        } else {
            emailService.sendConfirmationEmail(emailPreferences.getEmail(), "Welcome to Bookstore!", emailService.buildPromotionEmailBody());
        }
        return emailPreferences;
    }

    public EmailPreferences updateByEmail(EmailPreferences emailPreferences, EmailPreferencesRequestDto dto) {
        emailPreferences.setFirstName(Optional.ofNullable(dto.getFirstName()).orElse(emailPreferences.getFirstName()));
        emailPreferences.setLastName(Optional.ofNullable(dto.getLastName()).orElse(emailPreferences.getLastName()));
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = dto.getCategoryIds().stream().map(categoryService::findById).collect(Collectors.toSet());
            emailPreferences.setCategories(categories.stream().map(category -> new EmailCategoryRef(category.getId(), category.getName())).collect(Collectors.toSet()));
        } else {
            emailPreferences.setCategories(Collections.emptySet());
        }
        if (dto.getEmailTopicOptions() != null) {
            emailPreferences.setEmailTopics(dto.getEmailTopicOptions());
        } else {
            emailPreferences.setEmailTopics(Collections.emptyList());
        }
        return emailPreferencesRepository.save(emailPreferences);
    }

    public void unsubscribe(String email) {
        var optional = findByEmail(email);
        if (optional.isPresent()) {
            emailPreferencesRepository.delete(optional.get());
        } else {
            throw new CustomNoResultException(EmailPreferences.class, EMAIL, email);
        }
    }
}
