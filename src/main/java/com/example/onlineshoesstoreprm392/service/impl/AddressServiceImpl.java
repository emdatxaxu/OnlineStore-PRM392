package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Address;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.AddressMapper;
import com.example.onlineshoesstoreprm392.payload.AddressDto;
import com.example.onlineshoesstoreprm392.repository.AddressRepository;
import com.example.onlineshoesstoreprm392.repository.CartRepository;
import com.example.onlineshoesstoreprm392.repository.UserRepository;
import com.example.onlineshoesstoreprm392.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AddressServiceImpl implements AddressService {
    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private AddressMapper addressMapper;

    @Override
    public AddressDto createAddress(AddressDto addressDto) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();

        Address address = new Address();
        address.setAddress(addressDto.getAddress());
        address.setUser(user);
        address.setDefault(true);

        address = addressRepository.save(address);


        return addressMapper.toAddressDto(address);
    }

    @Override
    public void setAddressDefault(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        address.setDefault(true);
        addressRepository.save(address);
    }
}
