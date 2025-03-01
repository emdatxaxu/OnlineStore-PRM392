package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.AddressDto;

public interface AddressService {
    AddressDto createAddress(AddressDto addressDto);
    void setAddressDefault(Long addressId);
}
