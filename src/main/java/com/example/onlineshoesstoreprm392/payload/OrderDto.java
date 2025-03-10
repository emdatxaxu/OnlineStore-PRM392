package com.example.onlineshoesstoreprm392.payload;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    Long id;

    String fullname;

    String phoneNumber;

    String email;

    String address;

    BigDecimal totalPrice;

    Timestamp orderDate;

    String status;

    Long userId;
}
