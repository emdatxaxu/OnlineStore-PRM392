package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Image;
import com.example.onlineshoesstoreprm392.payload.ImageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image toImage(ImageDto imageDto);
    ImageDto toImageDto(Image image);
}
