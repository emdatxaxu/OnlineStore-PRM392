package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Address;
import com.example.onlineshoesstoreprm392.entity.Order;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.mapper.OrderMapper;
import com.example.onlineshoesstoreprm392.payload.OrderDto;
import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.payload.ProfileDto;
import com.example.onlineshoesstoreprm392.repository.AddressRepository;
import com.example.onlineshoesstoreprm392.repository.OrderRepository;
import com.example.onlineshoesstoreprm392.repository.UserRepository;
import com.example.onlineshoesstoreprm392.service.AddressService;
import com.example.onlineshoesstoreprm392.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;

    public UserServiceImpl(AddressRepository addressRepository, UserRepository userRepository,
                           OrderRepository orderRepository, OrderMapper orderMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public ProfileDto getProfile() {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();
        Address address = addressRepository.findByUserId(user.getId()).orElse(null);

        ProfileDto profileDto = toDto(user);
        if(address != null) profileDto.setAddress(address.getAddress());

        return profileDto;
    }

    @Override
    public PageableResponse<ProfileDto> getAllUserProfiles(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<User> users = userRepository.findByRoles(1L, pageable);

        //get content for page object
        List<User> listOfUsers = users.getContent();

        List<ProfileDto> content =  listOfUsers.stream().map(user -> toDto(user))
                .collect(Collectors.toList());

        PageableResponse response = new PageableResponse<ProfileDto>(content, users.getNumber(),
                users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());

        return response;
    }

    @Override
    public PageableResponse<OrderDto> getOrdersByUser(int pageNo, int pageSize, String sortBy, String sortDir) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);

        List<Order> orderList = orders.getContent();

        List<OrderDto> content = orderList.stream().map(order -> orderMapper.toOrderDto(order))
                .collect(Collectors.toList());

        PageableResponse orderResponse = new PageableResponse<OrderDto>(content, orders.getNumber(),
                orders.getSize(), orders.getTotalElements(), orders.getTotalPages(), orders.isLast());

        return orderResponse;

    }

    private ProfileDto toDto(User user){
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(user.getId());
        profileDto.setName(user.getName());
        profileDto.setUsername(user.getUsername());
        profileDto.setEmail(user.getEmail());
        profileDto.setPhoneNumber(user.getPhoneNumber());
        profileDto.setAddress("");
        return profileDto;
    }
}
