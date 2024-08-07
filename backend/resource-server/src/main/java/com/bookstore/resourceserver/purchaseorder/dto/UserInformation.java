package com.bookstore.resourceserver.purchaseorder.dto;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
