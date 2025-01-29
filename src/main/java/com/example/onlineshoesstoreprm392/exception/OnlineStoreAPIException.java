package com.example.onlineshoesstoreprm392.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class OnlineStoreAPIException extends RuntimeException{
    HttpStatus status;
    String message;

    public OnlineStoreAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}