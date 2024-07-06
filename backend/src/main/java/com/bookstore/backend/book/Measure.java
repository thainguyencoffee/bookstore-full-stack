package com.bookstore.backend.book;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record Measure(
        @NotNull(message = "The width of book must not be null")
        @Min(value = 40, message = "The width of book must greater than 40 mm.")
        @Max(value = 300, message = "The width of book must less than 300 mm.")
        double width,
        @NotNull(message = "The height of book must not be null.")
        @Min(value = 60, message = "The height of book must greater than 60 mm.")
        @Max(value = 400, message = "The height of book must less than 400 mm.")
        double height,
        @NotNull(message = "The thickness of book must not be null.")
        @Min(value = 1, message = "The thickness of book must greater than 1 mm.")
        @Max(value = 100, message = "The thickness of book must less than 100 mm.")
        double thickness,
        @NotNull(message = "The weight of book must not be null.")
        @Min(value = 170, message = "The lowest weight of book is 170 grams.")
        double weight // grams
) {
}
