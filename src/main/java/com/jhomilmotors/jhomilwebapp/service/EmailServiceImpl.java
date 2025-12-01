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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jhomilmotors@gmail.com");
            message.setTo(to);
            message.setSubject("Verifica tu correo - Jhomil Motors");
            message.setText(
                    "¡Gracias por registrarte!\n\n" +
                            "Por favor haz clic en el siguiente enlace para verificar tu cuenta:\n\n" +
                            verificationLink +
                            "\n\nSi no creaste una cuenta, ignora este correo."
            );

            mailSender.send(message);
            System.out.println("Correo enviado a: " + to);

        } catch (Exception e) {
            System.err.println("Error enviando correo en segundo plano: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
