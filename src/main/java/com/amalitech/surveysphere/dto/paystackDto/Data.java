package com.amalitech.surveysphere.dto.paystackDto;

@lombok.Data
public class Data {
    private int amount;
    private String currency;
    private String transactionDate;
    private String start;
    private String status;
    private Integer quantity;
    private String reference;
    private String domain;
    private String metadata;
    private String gatewayResponse;
    private String message;
    private String channel;
    private String ipAddress;
    private Long integration;
    private int fees;
    private Authorization authorization;
    private Customer customer;
    private String subscription_code;
    private String email_token;
    private Object plan;
    private Long id;
}
