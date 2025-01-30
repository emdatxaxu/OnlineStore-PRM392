package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.LoginDto;
import com.example.onlineshoesstoreprm392.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
