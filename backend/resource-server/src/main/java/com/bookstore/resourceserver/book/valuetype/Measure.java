package com.bookstore.resourceserver.book.valuetype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Measure {
    private double width;
    private double height;
    private double thickness;
    private double weight;
}
