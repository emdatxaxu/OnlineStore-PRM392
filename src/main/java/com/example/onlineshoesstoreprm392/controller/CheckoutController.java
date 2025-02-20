package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.payload.RecipientInfoDto;
import com.example.onlineshoesstoreprm392.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;
import vn.payos.util.SignatureUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private CheckoutService checkoutService;
    private PayOS payOS;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    public CheckoutController(CheckoutService checkoutService, PayOS payOS) {
        this.checkoutService = checkoutService;
        this.payOS = payOS;
    }

    //Each request to the server is served by a separate thread already ,
    // so you have no need to provide @Async on the controller method.

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
    @PostMapping("/payment-info")
    public ResponseEntity completePayment(@RequestBody Webhook webhook){
        checkoutService.completePayment(webhook);
        String signature = null;
        try {
            signature = SignatureUtils.createSignatureFromObj(webhook.getData(),checksumKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        webhook.setSuccess(true);
        webhook.setSignature(signature);
        return ResponseEntity.ok(webhook);
    }
}
