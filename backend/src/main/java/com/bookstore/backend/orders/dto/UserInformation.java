package com.bookstore.backend.orders.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {

    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    @NotBlank(message = "Address is required")
    private String address;

}
