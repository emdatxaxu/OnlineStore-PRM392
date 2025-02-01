package com.example.onlineshoesstoreprm392.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    Long id;

    @NotEmpty
    @Size(min = 2, message = "Product name should has at least 2 characters.")
    String name;

    @Positive
    BigDecimal price;

    @NotEmpty
    @Size(min = 10, message = "Product description should has at least 10 characters.")
    String description;

    Timestamp created_at;

    Timestamp updated_at;

    boolean deleted;

    @NotEmpty
    List<InventoryDto> inventories;

    List<FeedbackDto> feedbacks;

    @NotEmpty
    List<ImageDto> images;

    @NotNull
    Long categoryId;

}
