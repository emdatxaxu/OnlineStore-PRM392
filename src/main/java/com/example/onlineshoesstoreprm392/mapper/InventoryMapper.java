package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Inventory;
import com.example.onlineshoesstoreprm392.payload.InventoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toInventory(InventoryDto inventoryDto);
    InventoryDto toInventoryDto(Inventory inventory);
}
