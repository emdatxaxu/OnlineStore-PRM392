package com.example.onlineshoesstoreprm392.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipientInfoDto {

    @NotBlank
    String fullname;

    @NotBlank
    String phoneNumber;

    @NotBlank
    String address;

    CartDto cart;
}
