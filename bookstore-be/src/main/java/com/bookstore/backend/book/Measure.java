package com.bookstore.backend.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Measure {
    private double width;
    private double height;
    private double thickness;
    private double weight;
}
