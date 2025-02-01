package com.example.onlineshoesstoreprm392.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    List<ProductDto> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean last;
}
