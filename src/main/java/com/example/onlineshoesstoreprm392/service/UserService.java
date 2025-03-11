package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.OrderDto;
import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProfileDto;

public interface UserService {
    ProfileDto getProfile();
    PageableResponse<ProfileDto> getAllUserProfiles(int pageNo, int pageSize, String sortBy, String sortDir);
    PageableResponse<OrderDto>  getOrdersByUser(int pageNo, int pageSize, String sortBy, String sortDir);
}
