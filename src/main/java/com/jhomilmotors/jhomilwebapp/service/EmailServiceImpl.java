package com.jhomilmotors.jhomilwebapp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verifica tu correo para activar tu cuenta");
        message.setText(
                "¡Gracias por registrarte!\n\n" +
                        "Por favor haz clic en el siguiente enlace para verificar tu cuenta (o pégalo en el navegador):\n\n" +
                        verificationLink +
                        "\n\nSi no creaste una cuenta, ignora este correo."
        );
        mailSender.send(message);
    }
}
