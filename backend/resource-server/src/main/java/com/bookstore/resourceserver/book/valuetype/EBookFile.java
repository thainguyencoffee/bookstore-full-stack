package com.bookstore.resourceserver.book.valuetype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EBookFile {
    private String url;
    private Integer fileSize;
    private String format;
}
