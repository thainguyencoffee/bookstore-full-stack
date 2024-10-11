package com.bookstorefullstack.bookstore.book.valuetype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("ebook_file")
public class EBookFile {
    @Id
    private Long id;
    private String url;
    private Integer fileSize;
    private String format;
}
