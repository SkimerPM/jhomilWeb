package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import com.jhomilmotors.jhomilwebapp.repository.OrderRepository;
import com.jhomilmotors.jhomilwebapp.repository.PaymentRepository;

import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Preference crearPreferencia(String codigoPedido) throws Exception {

        // 1. Obtener pedido real
        Order pedido = orderRepository.findByCodigo(codigoPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con código: " + codigoPedido));

        // 2. Convertir cada OrderItem en un item de Mercado Pago
        List<PreferenceItemRequest> itemsMP = pedido.getItems().stream()
                .map(item -> PreferenceItemRequest.builder()
                        .title(
                                item.getVariante().getProduct().getNombre()
                                        + " - SKU "
                                        + item.getVariante().getSku()
                        )
                        .quantity(item.getCantidad())
                        .unitPrice(item.getVariante().getPrecio())
                        .currencyId("PEN")
                        .build()
                )
                .collect(Collectors.toList());
        // 3. URLs de retorno
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://tu-dominio.com/pago-success?pedido=" + pedido.getCodigo())
                .pending("https://tu-dominio.com/pago-pending?pedido=" + pedido.getCodigo())
                .failure("https://tu-dominio.com/pago-failure?pedido=" + pedido.getCodigo())
                .build();

        // 4. Crear preferencia
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMP)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(pedido.getCodigo())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // 5. Registrar el pago (PENDIENTE)
        Payment pago = Payment.builder()
                .pedido(pedido)
                .metodo(PaymentMethod.MERCADO_PAGO)
                .monto(pedido.getTotal())
                .estado(PaymentStatus.PENDIENTE)
                .referenciaExterna(preference.getId())   // preference_id de MP
                .build();

        paymentRepository.save(pago);

        return preference;
    }
}
