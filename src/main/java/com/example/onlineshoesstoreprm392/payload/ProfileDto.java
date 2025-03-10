package com.example.onlineshoesstoreprm392.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDto {

    Long id;

    String name;

    String username;

    String email;

    String phoneNumber;

    String address;
}
