package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.payload.RecipientInfoDto;
import com.example.onlineshoesstoreprm392.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private CheckoutService checkoutService;

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RecipientInfoDto> checkout(){
        return ResponseEntity.ok(checkoutService.checkout());
    }

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutResponseData> confirmCheckout(@Valid @RequestBody RecipientInfoDto recipientInfoDto){
        return ResponseEntity.ok(checkoutService.confirmCheckout(recipientInfoDto));
    }

    //webhook
    @GetMapping("/payment-info")
    public ResponseEntity completePayment(@RequestBody Webhook webhook){
        checkoutService.completePayment(webhook);
        return ResponseEntity.noContent().build();
    }
}
