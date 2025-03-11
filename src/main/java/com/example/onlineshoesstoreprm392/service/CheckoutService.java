package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.RecipientInfoDto;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;

import java.io.IOException;

public interface CheckoutService {
    CheckoutResponseData confirmCheckout(RecipientInfoDto recipientInfo);
    RecipientInfoDto checkout();
    void completePayment(Webhook webhook) throws IOException;
}
