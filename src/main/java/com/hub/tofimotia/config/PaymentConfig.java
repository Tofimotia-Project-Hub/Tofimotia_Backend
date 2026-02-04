package com.hub.tofimotia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    
    private boolean mockMode = true; // Set to false when integrating real payment gateways
    
    private Paystack paystack = new Paystack();
    private Flutterwave flutterwave = new Flutterwave();
    private Stripe stripe = new Stripe();
    
    @Data
    public static class Paystack {
        private String publicKey = "pk_test_mock";
        private String secretKey = "sk_test_mock";
        private String baseUrl = "https://api.paystack.co";
    }
    
    @Data
    public static class Flutterwave {
        private String publicKey = "FLWPUBK_TEST-mock";
        private String secretKey = "FLWSECK_TEST-mock";
        private String baseUrl = "https://api.flutterwave.com/v3";
    }
    
    @Data
    public static class Stripe {
        private String publicKey = "pk_test_mock";
        private String secretKey = "sk_test_mock";
        private String baseUrl = "https://api.stripe.com/v1";
    }
}