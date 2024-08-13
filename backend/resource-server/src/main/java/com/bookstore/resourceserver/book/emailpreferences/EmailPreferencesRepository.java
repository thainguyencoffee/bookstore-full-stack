package com.bookstore.resourceserver.book.emailpreferences;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface EmailPreferencesRepository extends ListCrudRepository<EmailPreferences, Long> {

    @Query("select e.* from email_preferences e join email_preferences_category epc on e.id = epc.email_preferences where epc.category = :categoryId")
    Set<EmailPreferences> findByCategoryId(@Param("categoryId") Long categoryId);

    Optional<EmailPreferences> findByEmail(String email);

}
