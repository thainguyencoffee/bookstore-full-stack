package com.bookstorefullstack.bookstore.emailpreference;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EmailPreferencesRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private List<Long> categoryIds;
    private List<EmailTopic> emailTopicOptions;

    public EmailPreferences toEmailPreferences() {
        EmailPreferences emailPreferences = new EmailPreferences();
        emailPreferences.setEmail(this.email);
        emailPreferences.setFirstName(this.firstName);
        emailPreferences.setLastName(this.lastName);
        if (this.emailTopicOptions != null) {
            emailPreferences.setEmailTopics(this.emailTopicOptions);
        }
        return emailPreferences;
    }
}
