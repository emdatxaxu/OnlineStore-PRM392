package com.example.onlineshoesstoreprm392.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto {

    @NotEmpty
    String name;

    @Size(min = 5, max = 50)
    @NotEmpty
    String username;

    @Email
    String email;

    @NotEmpty
    String password;

    @Pattern(regexp="(^$|[0-9]{10})")
    String phoneNumber;
}
