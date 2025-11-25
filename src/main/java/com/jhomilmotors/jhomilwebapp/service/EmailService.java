package com.jhomilmotors.jhomilwebapp.service;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
}
