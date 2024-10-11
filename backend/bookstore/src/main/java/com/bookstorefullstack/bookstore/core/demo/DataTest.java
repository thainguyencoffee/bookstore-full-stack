package com.bookstorefullstack.bookstore.core.demo;

import com.bookstorefullstack.bookstore.author.Author;
import com.bookstorefullstack.bookstore.author.AuthorRepository;
import com.bookstorefullstack.bookstore.book.Book;
import com.bookstorefullstack.bookstore.book.BookRepository;
import com.bookstorefullstack.bookstore.book.EBook;
import com.bookstorefullstack.bookstore.book.PrintBook;
import com.bookstorefullstack.bookstore.book.valuetype.*;
import com.bookstorefullstack.bookstore.category.Category;
import com.bookstorefullstack.bookstore.category.CategoryRepository;
import com.bookstorefullstack.bookstore.emailpreference.EmailPreferences;
import com.bookstorefullstack.bookstore.emailpreference.EmailPreferencesRepository;
import com.bookstorefullstack.bookstore.emailpreference.EmailTopic;
import com.bookstorefullstack.bookstore.book.quotation.Quotation;
import com.bookstorefullstack.bookstore.book.quotation.QuotationRepository;
import com.bookstorefullstack.bookstore.core.valuetype.Price;
import com.bookstorefullstack.bookstore.core.valuetype.UserInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Profile("datatest")
@Slf4j
@RequiredArgsConstructor
public class DataTest {

    private static final Random random = new Random();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final QuotationRepository quotationRepository;
    private final CategoryRepository categoryRepository;
    private static final List<String> thumbnailsCategory = List.of(
            "https://cdn0.fahasa.com/media/wysiwyg/Thang-06-2024/icon_ManngaT06.png",
            "https://cdn0.fahasa.com/media/wysiwyg/Duy-VHDT/Icon_Balo_120x120.png",
            "https://cdn0.fahasa.com/media/wysiwyg/Thang-08-2024/Icon_Bitex_t8.png",
            "https://cdn0.fahasa.com/media/wysiwyg/icon-menu/Icon_SanPhamMoi_8px_1.png"
    );
    private final EmailPreferencesRepository emailPreferencesRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void setupDataTest() {
        clear();

        var categoryList = new ArrayList<Category>();
        for (int i = 0; i < 50; i++) {
            Category category = new Category();
            category.setName(generateRandomName());
            if (i > 0) {
                category.setParent(categoryList.get(i - 1));
            }
            category.setThumbnail(thumbnailsCategory.get(random.nextInt(thumbnailsCategory.size())));
            categoryRepository.save(category);
            categoryList.add(category);
        }

        var authorList = new ArrayList<Author>();
        for (Long number : generatedNumbers10Digits()) {
            Author author = new Author();
            UserInformation userInfo = new UserInformation();
            userInfo.setFirstName(generateRandomName());
            userInfo.setLastName(generateRandomName());
            userInfo.setEmail(generateRandomEmail());
            userInfo.setPhone(new Phone(String.valueOf(number).substring(1), 84));
            author.setUserInformation(userInfo);
            author.setJobTitle(generateJobTitle());
            author.setAbout(aboutAuthor);
            authorRepository.save(author);
            authorList.add(author);
        }

        for (Category category : categoryRepository.findAll()) {
            EmailPreferences emailPreferences = new EmailPreferences();
            emailPreferences.setEmail(generateRandomEmail());
            emailPreferences.addAllCategories(Set.of(category));
            emailPreferences.setFirstName("Nguyen");
            emailPreferences.setLastName("Thai");
            emailPreferences.setEmailTopics(List.of(EmailTopic.NEW_RELEASES));
            emailPreferencesRepository.save(emailPreferences);
        }

        // Set up random time for purchase_at
        Instant start = LocalDateTime.of(2024, Month.JUNE, 10, 0, 0).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(2024, Month.AUGUST, 10, 23, 59).toInstant(ZoneOffset.UTC);
        for (long isbn : generatedNumbers10Digits()) {
            long timeRandomMillis = ThreadLocalRandom.current().nextLong(start.toEpochMilli(), end.toEpochMilli());
            Instant randomTime = Instant.ofEpochMilli(timeRandomMillis);
            Book book = buildExampleBook(isbn, randomTime, random.nextInt(10) + 5, authorList, categoryList);
            createTwoVariantOfBook(book);
            bookRepository.save(book);
            createThreeQuotesOfBook(book);
        }

    }

    private void createThreeQuotesOfBook(Book book) {
        for (Author author: authorRepository.findAllById(book.getAuthorIds())) {
            var quote = new Quotation();
            quote.setIsbn(book.getIsbn());
            quote.setAuthorId(author.getId());
            quote.setJobTitle(author.getJobTitle());
            quote.setText("This is a fantastic resource for getting up to speed on LLMs fast.");
            quotationRepository.save(quote);
        }
    }

    private void createTwoVariantOfBook(Book book) {
        Instant now = Instant.now();
        Price eBookPrice = new Price(1200000L, 1000000L);
        Price printBookPrice = new Price(1500000L, 1200000L);
        BookProperties eBookProps = new BookProperties();
        eBookProps.setPrice(eBookPrice);
        eBookProps.setPurchases(random.nextInt(50));
        eBookProps.setNumberOfPages(450);
        eBookProps.setReleaseDate(now);
        eBookProps.setPublicationDate(now.plus(15, ChronoUnit.DAYS));

        BookProperties printBookProps = new BookProperties();
        printBookProps.setPrice(printBookPrice);
        printBookProps.setPurchases(random.nextInt(50));
        printBookProps.setNumberOfPages(452);
        printBookProps.setReleaseDate(now);
        printBookProps.setPublicationDate(now.plus(15, ChronoUnit.DAYS));


        String url = "https://bookstore-bucket.sgp1.digitaloceanspaces.com/bookstore-bucket/book/1234567890/files/Thomas%20Vitale%20-%20Cloud%20Native%20Spring%20in%20Action_%20With%20Spring%20Boot%20and%20Kubernetes-Manning.pdf";
        var eBook = new EBook();
        var eBookFile = new EBookFile();
        eBookFile.setUrl(url);
        eBookFile.setFileSize(19820);
        eBookFile.setFormat("pdf");
        eBook.addEBookFile(eBookFile);
        eBook.setProperties(eBookProps);
        book.setEBook(eBook);

        var printBook = new PrintBook();
        printBook.setProperties(printBookProps);
        printBook.setCoverType(CoverType.PAPERBACK);
        printBook.setMeasure(new Measure(12, 12, 12,12));
        printBook.setInventory(120);
        book.setPrintBook(printBook);
    }

    private void clear() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        authorRepository.deleteAll();
        emailPreferencesRepository.deleteAll();
    }

    private Set<Long> generatedNumbers10Digits() {
        Set<Long> generatedNumbers = new HashSet<>();

        while (generatedNumbers.size() < 50) {
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            generatedNumbers.add(number);
        }
        return generatedNumbers;
    }

    private static Book buildExampleBook(Long isbn, Instant purchaseAt, Integer purchases, List<Author> authors, List<Category> categoryList) {
        var book = new Book();
        book.setIsbn(String.valueOf(isbn));
        book.setTitle(generateRandomName() + isbn);
        book.setCategory(categoryList.get(random.nextInt(categoryList.size())));
        book.addAuthor(generateAuthor(authors));
        book.setPublisher(generateRandomName());
        book.setSupplier(generateRandomName());
        book.setDescription(description);
        book.setEdition(1);
        book.setLanguage(Language.ENGLISH);
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

    private static final String[] JOB_TITLE = {
            "Software Engineering", "Teacher", "Computer Science", "Data Science", "Freelancer", "Project Manager", "Business Analyst",
            "Devops", "IT Helpdesk", "Architecture Solution"
    };

    public static final String description = """
            <span class="fs-5 fw-bold d-block">Main content focus something!</span>
                  <span class="fw-lighter fs-6 d-block">
                    In Build a Large Language Model (from Scratch), you’ll discover how LLMs work from the inside out. In this insightful book, bestselling author Sebastian Raschka guides you step by step through creating your own LLM, explaining each stage with clear text, diagrams, and examples. You’ll go from the initial design and creation to pretraining on a general corpus, all the way to finetuning for specific tasks.
                    <ul>
                      <li>Build a Large Language Model (from Scratch) teaches you how to:            </li>
                      <li>Plan and code all the parts of an LLM            </li>
                      <li>Prepare a dataset suitable for LLM training            </li>
                      <li>Finetune LLMs for text classification and with your own data            </li>
                      <li>Apply instruction tuning techniques to ensure your LLM follows instructions            </li>
                      <li>Load pretrained weights into an LLM            </li>
                      <li>The large language models (LLMs) that power cutting-edge AI tools like ChatGPT, Bard, and Copilot seem like a miracle, but they’re not magic. This book demystifies LLMs by helping you build your own from scratch. You’ll get a unique and valuable insight into how LLMs work, learn how to evaluate their quality, and pick up concrete techniques to finetune and improve them.
                      </li>
                      <li>The process you use to train and develop your own small-but-functional model in this book follows the same steps used to deliver huge-scale foundation models like GPT-4. Your small-scale LLM can be developed on an ordinary laptop, and you’ll be able to use it as your own personal assistant.
                      </li>
                    </ul>
                  </span>
            </span>
            """;

    public static final String aboutAuthor = """
            Josh (@starbuxman) has been the first Spring Developer Advocate since 2010. Josh is a Java Champion, author of 6 books (including O'Reilly's Cloud Native Java: Designing Resilient Systems with Spring Boot, Spring Cloud, and Cloud Foundry and the upcoming Reactive Spring) and numerous best-selling video trainings (including "Building Microservices with Spring Boot Livelessons" with Spring Boot co-founder Phil Webb), and an open-source contributor (Spring Boot, Spring Integration, Spring Cloud, Activiti and Vaadin), a podcaster (A Bootiful Podcast) and a screencaster (see the "Spring Tips" playlist on spring.io/video)
            """;

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

    public static String generateRandomEmail() {
        return generateRandomName() + "@gmail.com";
    }

    public static String generateJobTitle() {
        return JOB_TITLE[random.nextInt(JOB_TITLE.length)];
    }

    public static Author generateAuthor(List<Author> authors) {
        return authors.get(random.nextInt(authors.size()));
    }

}