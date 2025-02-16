package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.*;
import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.CartMapper;
import com.example.onlineshoesstoreprm392.payload.RecipientInfoDto;
import com.example.onlineshoesstoreprm392.repository.*;
import com.example.onlineshoesstoreprm392.service.CheckoutService;
import com.example.onlineshoesstoreprm392.utils.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private AddressRepository addressRepository;
    private InventoryRepository inventoryRepository;
    private CartMapper cartMapper;
    private OrderRepository orderRepository;
    private PayOS payOS;

    public CheckoutServiceImpl(UserRepository userRepository,
                               CartRepository cartRepository,
                               AddressRepository addressRepository,
                               InventoryRepository inventoryRepository,
                               CartMapper cartMapper,
                               OrderRepository orderRepository,
                               PayOS payOS) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartMapper = cartMapper;
        this.orderRepository = orderRepository;
        this.payOS = payOS;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public CheckoutResponseData confirmCheckout(RecipientInfoDto recipientInfo) {

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        //lay ra cart cua customer
        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        List<Long> inventoryIds = cart.getCartItems().stream().map(item -> item.getInventory().getId())
                .collect(Collectors.toList());

        ////////////////////////////////////////////////////////////////////////////
        //Using Optimistic lock for handle concurrent buying request on one product
        //If A and B buy at the same time and the number of products is still enough for both,
        // a conflict will still occur but both people can continue to buy.
        // If the number of products is not enough for both, only one person can buy,
        // the other person will be notified of an error.
        boolean hasConflict = false;

        do{
            try {
                Map<Long, Inventory> inventories = inventoryRepository.findAllById(inventoryIds).stream()
                        .collect(Collectors.toMap(Inventory::getId, inventory -> inventory));

                //kiem tra so luong
                for(CartItem item : cart.getCartItems()){
                    Inventory inventory = inventories.get(item.getInventory().getId());
                    //neu san pham trong kho khong du de ban
                    if(item.getQuantity() > inventory.getUnitsInStock()){
                        throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                                "Only "+item.getInventory().getUnitsInStock()+" left in stock.");
                    }
                    //cap nhat inventory
                    inventory.setUnitsInStock( inventory.getUnitsInStock() - item.getQuantity() );
                }
                hasConflict = false;
                inventoryRepository.saveAll(inventories.values());
            }catch (ObjectOptimisticLockingFailureException e){
                hasConflict = true;
            }
        }while(hasConflict);

        //create Order and OrderItem base on cart and cart item
        Order order = Order.builder()
                .fullname(recipientInfo.getFullname())
                .phoneNumber(recipientInfo.getPhoneNumber())
                .email(user.getEmail())
                .address(recipientInfo.getAddress())
                .totalPrice(cart.getTotalPrice())
                .orderDate(new Timestamp(System.currentTimeMillis()))
                .status(OrderStatus.PENDING)
                .user(user)
                .build();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getCartItems()){
            //create an empty inventory object only have id to set foreign key for order item
            // => so I can avoid 'lost update'
            Inventory inventory = new Inventory();
            inventory.setId(item.getInventory().getId());

            OrderItem orderItem = OrderItem.builder()
                    .name(item.getName())
                    .image(item.getImage())
                    .unitPrice(item.getUnitPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getTotalPrice())
                    .product(item.getProduct())
                    .inventory(inventory)
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        return getCheckoutData(order);
    }


    @Override
    public RecipientInfoDto checkout() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();

        //lay ra cart cua customer
        User user = userRepository.findByEmail(email).get();

        Address address = addressRepository.findByUserId(user.getId())
                .orElse(new Address(null, "",false,null));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        //kiem tra inventory
        for(CartItem item : cart.getCartItems()){
            if(item.getQuantity() > item.getInventory().getUnitsInStock()){//san pham trong kho khong du de ban
                throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                        "Only "+item.getInventory().getUnitsInStock()+" left in stock.");
            }
        }

        return new RecipientInfoDto(user.getName(), user.getPhoneNumber(),
                address.getAddress(), cartMapper.toCartDto(cart));
    }

    //nhan du lieu tra ve tu cong thanh toan de hoan tat thanh toan
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void completePayment(Webhook webhook) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        //lay ra cart cua customer
        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));

        //webhook domainUrl+"/api/checkout/payment-info"
        //lay data ma PayOS tra ve webhook
        WebhookData webhookData;
        PaymentLinkData paymentLinkData;
        try {
            webhookData = payOS.verifyPaymentWebhookData(webhook);
            paymentLinkData = payOS.getPaymentLinkInformation(webhookData.getOrderCode());
        } catch (Exception e) {
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST, "Some errors occur when completing payment");
        }

        String paymentStatus = paymentLinkData.getStatus();

        //get user's order
        Order order = orderRepository.findById(webhookData.getOrderCode())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id",webhookData.getOrderCode()));

        //update order status
        order.setStatus(paymentStatus);

        //if payment is success
        if(paymentStatus.equals(OrderStatus.PAID)){
            //clear cart
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
        if(paymentStatus.equals(OrderStatus.CANCELLED)){
            //refund the units in stock for inventory
            List<Long> inventoryIds = order.getOrderItems().stream().map(item -> item.getInventory().getId())
                    .collect(Collectors.toList());
            boolean hasConflict = false;
            do{
                try {
                    Map<Long, Inventory> inventories = inventoryRepository.findAllById(inventoryIds).stream()
                            .collect(Collectors.toMap(Inventory::getId, inventory -> inventory));
                    for(OrderItem item : order.getOrderItems()){
                        Inventory inventory = inventories.get(item.getInventory().getId());
                        //cap nhat inventory
                        inventory.setUnitsInStock( inventory.getUnitsInStock() + item.getQuantity() );
                    }
                    hasConflict = false;
                    inventoryRepository.saveAll(inventories.values());
                }catch (ObjectOptimisticLockingFailureException e){
                    hasConflict = true;
                }
            }while(hasConflict);
        }
        orderRepository.save(order);
    }


    private CheckoutResponseData getCheckoutData(Order order){
        //create payment link
        List<ItemData> itemDataList = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()){
            ItemData itemData = ItemData.builder()
                    .name(orderItem.getName())
                    .quantity(orderItem.getQuantity())
                    .price(orderItem.getTotalPrice().intValue()).build();
            itemDataList.add(itemData);
        }
        PaymentData paymentData = PaymentData.builder()
                .orderCode(order.getId())
                .amount(order.getTotalPrice().intValue()).description("Thanh toan don hang")
                .returnUrl("")
                .cancelUrl("")
                .items(itemDataList).build();
        CheckoutResponseData checkoutResponseData;
        try {
             checkoutResponseData = payOS.createPaymentLink(paymentData);
        } catch (Exception e) {
            throw new OnlineStoreAPIException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to make payment due to some arising errors.");
        }
        return checkoutResponseData;
    }

}
