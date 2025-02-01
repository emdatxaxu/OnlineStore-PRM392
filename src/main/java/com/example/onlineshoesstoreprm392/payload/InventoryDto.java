package com.example.onlineshoesstoreprm392.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryDto {
    Long id;

    int size;

    String color;

    int unitsInStock;
}
