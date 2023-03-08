package com.app.IVAS.service;


import java.net.URISyntaxException;

public interface SmsService {
    void sendSms(String recipient, String message) throws URISyntaxException;
}
