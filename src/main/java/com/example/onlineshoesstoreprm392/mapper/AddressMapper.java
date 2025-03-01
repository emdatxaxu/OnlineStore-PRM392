package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Address;
import com.example.onlineshoesstoreprm392.entity.Cart;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.payload.AddressDto;
import com.example.onlineshoesstoreprm392.payload.CartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressDto addressDto);

    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserId")
    AddressDto toAddressDto(Address address);

    @Named("mapUserId")
    default Long mapUserId(User user){ return user.getId();}
}
