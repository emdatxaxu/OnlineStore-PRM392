package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.*;
import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.CartItemMapper;
import com.example.onlineshoesstoreprm392.mapper.CartMapper;
import com.example.onlineshoesstoreprm392.mapper.ProductMapper;
import com.example.onlineshoesstoreprm392.payload.CartDto;
import com.example.onlineshoesstoreprm392.payload.CartItemDto;
import com.example.onlineshoesstoreprm392.repository.*;
import com.example.onlineshoesstoreprm392.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private CartItemMapper cartItemMapper;
    private CartMapper cartMapper;
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private UserRepository userRepository;
    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;
    private ProductMapper productMapper;

    public CartServiceImpl(CartItemMapper cartItemMapper,
                           CartMapper cartMapper,
                           CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           InventoryRepository inventoryRepository,
                           ProductRepository productRepository,
                           ProductMapper productMapper) {
        this.cartItemMapper = cartItemMapper;
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public CartDto getCartByUser() {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        return cartMapper.toCartDto(cart);
    }

    @Override
    public CartDto addToCart(CartItemDto cartItemDto) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        Product product = productRepository.findById(cartItemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItemDto.getProductId()));

        Inventory inventory = inventoryRepository.findById(cartItemDto.getInventory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", cartItemDto.getInventory().getId()));

        //compare CartItem's quantity and inventory
        if(cartItemDto.getQuantity() > inventory.getUnitsInStock()){//khong du san pham
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Insufficient product quantity");
        }

        CartItem cartItem = new CartItem();
        cartItem.setName(product.getName());
        cartItem.setImage(product.getImages().get(0).getImage());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setInventory(inventory);

        cartItem = cartItemRepository.save(cartItem);

        if(cart.getCartItems() == null){
            cart.setCartItems(new ArrayList<>());
        }

        cart.getCartItems().add(cartItem);

        updateCartTotalPrice(cart);

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toCartDto(updatedCart);
    }

    @Override
    public CartDto removeFromCart(Long cartItemId) {
        //kiem tra xem item do co ton tai ko -> kiem tra xem cart do co phai cua user do ko->xoa item, cap nhat total price cua cart
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();
        //lay cart cua user ra
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        //kiem tra xem item do co ton tai ko
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        //kiem tra xem cartitem do co nam trong cart cua user do ko
        if(!cartItem.getCart().getId().equals(cart.getId())){
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        cart.getCartItems().remove(cartItem);
        updateCartTotalPrice(cart);

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toCartDto(updatedCart);
    }

    @Override
    public CartDto updateCart(CartItemDto cartItemDto, Long cartItemId) {
        return null;
    }

    private void updateCartTotalPrice(Cart cart){
        BigDecimal sum = new BigDecimal(0);

        for(CartItem item : cart.getCartItems()){
            sum = sum.add(item.getTotalPrice());
        }

        cart.setTotalPrice(sum);
    }
}
