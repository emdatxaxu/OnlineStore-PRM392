package com.example.onlineshoesstoreprm392;

import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import vn.payos.PayOS;

@SpringBootApplication
public class OnlineShoesStorePrm392Application {

    @Value("${PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    @Value("${DOMAIN_URL}")
    private String domainUrl;


    //because I using Ngrok for go live and Ngrok giving random domain url every time I start it, so i have to use
    //this method in order to set the new webhook despite of going to PayOS official website to set my webhook
    //every time I run the server
    @Bean
    public PayOS payOS(){
        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);
        //confirm webhook
        String verifiedWebhookUrl;
        try {
            verifiedWebhookUrl = payOS.confirmWebhook(domainUrl+"/api/checkout/payment-info");
        } catch (Exception e) {
            throw new OnlineStoreAPIException(HttpStatus.SERVICE_UNAVAILABLE,"Cannot confirm webhook");
        }
        return payOS;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoesStorePrm392Application.class, args);
    }

}
