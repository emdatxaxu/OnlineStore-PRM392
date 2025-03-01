package com.example.onlineshoesstoreprm392.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    Long id;

    @NotBlank
    String address;

    boolean isDefault;

    @NotNull
    Long userId;
}
