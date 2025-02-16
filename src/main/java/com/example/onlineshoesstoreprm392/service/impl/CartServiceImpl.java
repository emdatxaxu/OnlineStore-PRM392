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
import java.util.List;
import java.util.stream.Collectors;

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

    //normal case: add new item to cart
    //abnormal case: the item has already added to cart, user want to add more.
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

        //kiem tra xem co san pham tuong tu o trong cart chua, neu da co thi se addToCart se tang so luong cua san pham
        //o trong cart len, con neu khong thi addToCart se them item moi vao trong cart
        List<CartItem> sameCartItem = cart.getCartItems().stream()
                .filter(item -> item.getInventory().getId().equals(cartItemDto.getInventory().getId()))
                .collect(Collectors.toList());
        int itemQuantity = cartItemDto.getQuantity();

        if(sameCartItem.size() > 0){//abnormal case

            itemQuantity += sameCartItem.get(0).getQuantity();

            //compare item quantity and inventory
            if(itemQuantity > inventory.getUnitsInStock()){//khong du san pham
                throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Insufficient product quantity");
            }

            CartItem cartItem = sameCartItem.get(0);
            cartItem.setQuantity(itemQuantity);
            cartItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));

            updateCartTotalPrice(cart);

            Cart updatedCart = cartRepository.save(cart);

            return cartMapper.toCartDto(updatedCart);
        }


        //compare item quantity and inventory
        if(itemQuantity > inventory.getUnitsInStock()){//khong du san pham
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Insufficient product quantity");
        }

        //normal case
        CartItem cartItem = new CartItem();
        cartItem.setName(product.getName());
        cartItem.setImage(product.getImages().get(0).getImage());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setQuantity(itemQuantity);
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
        //kiem tra xem item do co ton tai ko
        // -> kiem tra xem cart do co phai cua user do ko
        //->kiem tra xem cartitem do co nam trong cart cua user do ko
        // ->xoa item, cap nhat total price cua cart
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
    public CartDto updateCart(CartItemDto cartItemDto, Long cartItemId) { //only update quantity
        //->kiem tra xem cartitem do co nam trong cart cua user do ko
        //->compare CartItem's quantity and inventory
        // ->cap nhat item, cap nhat total price cua cart

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        //kiem tra xem cartitem do co nam trong cart cua user do ko
        if(!cartItemDto.getCartId().equals(cart.getId())){
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        Inventory inventory = inventoryRepository.findById(cartItemDto.getInventory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", cartItemDto.getInventory().getId()));

        //get the item that need to be updated
        CartItem cartItem = cart.getCartItems().stream().filter(item -> item.getId().equals(cartItemId))
                .collect(Collectors.toList()).get(0);

        //compare CartItem's quantity and inventory
        if(cartItemDto.getQuantity() > inventory.getUnitsInStock()){//khong du san pham
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Insufficient product quantity");
        }
        else if(cartItemDto.getQuantity() <= 0){//->xoa item khoi cart
            cart.getCartItems().remove(cartItem);
        }
        else{ //cap nhat so luong va total price cua item
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            updateCartTotalPrice(cart);
        }
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toCartDto(updatedCart);
    }

    private void updateCartTotalPrice(Cart cart){
        BigDecimal sum = new BigDecimal(0);

        for(CartItem item : cart.getCartItems()){
            sum = sum.add(item.getTotalPrice());
        }

        cart.setTotalPrice(sum);
    }
}
