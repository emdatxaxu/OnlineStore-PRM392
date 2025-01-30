package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Role;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import com.example.onlineshoesstoreprm392.payload.LoginDto;
import com.example.onlineshoesstoreprm392.payload.RegisterDto;
import com.example.onlineshoesstoreprm392.repository.RoleRepository;
import com.example.onlineshoesstoreprm392.repository.UserRepository;
import com.example.onlineshoesstoreprm392.security.JwtTokenProvider;
import com.example.onlineshoesstoreprm392.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {

        //check for username exist
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }

        //check for email exist
        if(userRepository.existsByEmail(registerDto.getEmail())){
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setPhoneNumber(registerDto.getPhoneNumber());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User register successfully!";
    }
}
