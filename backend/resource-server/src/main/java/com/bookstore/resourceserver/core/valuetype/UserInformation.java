package com.bookstore.resourceserver.core.valuetype;

import com.bookstore.resourceserver.book.valuetype.Phone;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.relational.core.mapping.Embedded;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {

    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;
    @Embedded.Nullable
    private Phone phone;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
