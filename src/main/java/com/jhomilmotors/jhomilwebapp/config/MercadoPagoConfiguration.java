package com.jhomilmotors.jhomilwebapp.config;


import com.mercadopago.MercadoPagoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    public MercadoPagoConfiguration(
            @Value("${mercadopago.access-token}") String accessToken
    ) {
        MercadoPagoConfig.setAccessToken(accessToken);
        System.out.println("Mercado Pago inicializado correctamente");
    }
}