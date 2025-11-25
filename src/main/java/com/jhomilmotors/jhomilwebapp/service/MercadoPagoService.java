package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.*;
import com.jhomilmotors.jhomilwebapp.enums.OrderStatus;
import com.jhomilmotors.jhomilwebapp.enums.PaymentMethod;
import com.jhomilmotors.jhomilwebapp.enums.PaymentStatus;
import com.jhomilmotors.jhomilwebapp.repository.OrderRepository;
import com.jhomilmotors.jhomilwebapp.repository.PaymentRepository;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class MercadoPagoService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // 🔴 CAMBIA ESTO POR TU URL DE NGROK (Ej: https://abcd.ngrok-free.app)
    private final String NOTIFICATION_URL = "https://nonsocialistic-leaden-shaquana.ngrok-free.dev/api/v1/payments/webhook";

    // URL DE TU FRONTEND (REACT)
    private final String FRONTEND_URL = "https://jhomilweb.onrender.com"; // O el puerto 3000 si usas CreateReactApp

    public Preference crearPreferencia(String codigoPedido) throws Exception {

        // 1. Obtener pedido real
        Order pedido = orderRepository.findByCodigo(codigoPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con código: " + codigoPedido));

        // 2. Convertir items
        List<PreferenceItemRequest> itemsMP = pedido.getItems().stream()
                .map(item -> PreferenceItemRequest.builder()
                        .title(item.getVariante().getProduct().getNombre())
                        .quantity(item.getCantidad())
                        .unitPrice(item.getVariante().getPrecio())
                        .currencyId("PEN")
                        .build()
                )
                .collect(Collectors.toList());

        // 3. URLs de retorno (Para que el usuario vuelva a React)
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(FRONTEND_URL + "/pago-exitoso?pedido=" + pedido.getCodigo())
                .pending(FRONTEND_URL + "/pago-pendiente?pedido=" + pedido.getCodigo())
                .failure(FRONTEND_URL + "/pago-fallido?pedido=" + pedido.getCodigo())
                .build();

        // 4. Crear preferencia con NOTIFICATION_URL
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(itemsMP)
                // .autoReturn("approved")
                .backUrls(backUrls)
                .externalReference(pedido.getCodigo()) // IMPORTANTE: Aquí viaja el ID del pedido
                .notificationUrl(NOTIFICATION_URL)     // IMPORTANTE: Aquí avisará MP al Backend
                .build();

        System.out.println("🧐 REVISANDO URLS ANTES DE ENVIAR:");
        if (preferenceRequest.getBackUrls() != null) {
            System.out.println("✅ Success URL: " + preferenceRequest.getBackUrls().getSuccess());
        } else {
            System.err.println("🔥 EL OBJETO BACKURLS ES NULL (CÓDIGO VIEJO O ERROR)");
        }

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // 5. Registrar el pago inicial (PENDIENTE)
        // Verificamos si ya existe un pago pendiente para no duplicar si el usuario le da atrás y adelante
        boolean pagoExiste = paymentRepository.findByPedidoId(pedido.getId()).stream()
                .anyMatch(p -> p.getEstado() == PaymentStatus.PENDIENTE);

        if (!pagoExiste) {
            com.jhomilmotors.jhomilwebapp.entity.Payment pagoLocal = com.jhomilmotors.jhomilwebapp.entity.Payment.builder()
                    .pedido(pedido)
                    .metodo(PaymentMethod.MERCADO_PAGO)
                    .monto(pedido.getTotal())
                    .estado(PaymentStatus.PENDIENTE)
                    .referenciaExterna(preference.getId())
                    .fechaPago(LocalDateTime.now())
                    .build();
            paymentRepository.save(pagoLocal);
        }

        return preference;
    }

    /**
     * MÉTODO MÁGICO QUE ACTUALIZA EL ESTADO
     * Este método lo llamará tu Controller cuando reciba el Webhook
     */
    @Transactional
    public void procesarNotificacionPago(String paymentIdMP) {
        try {
            // 1. Consultar a Mercado Pago el estado real
            PaymentClient client = new PaymentClient();
            Payment mpPayment = client.get(Long.parseLong(paymentIdMP));

            String estadoMP = mpPayment.getStatus();
            String codigoPedido = mpPayment.getExternalReference(); // Recuperamos el código

            System.out.println("🔔 Webhook recibido. Pedido: " + codigoPedido + " | Estado MP: " + estadoMP);

            if ("approved".equals(estadoMP)) {
                // 2. Buscar el pedido
                Order pedido = orderRepository.findByCodigo(codigoPedido)
                        .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

                // 3. Actualizar estado del PEDIDO
                if (pedido.getEstado() != OrderStatus.PAGADO) {
                    pedido.setEstado(OrderStatus.PAGADO);
                    orderRepository.save(pedido);
                }

                // 4. Actualizar el PAGO en BD
                // Buscamos el pago asociado a este pedido que estaba pendiente
                List<com.jhomilmotors.jhomilwebapp.entity.Payment> pagos = paymentRepository.findByPedidoId(pedido.getId());

                // Si hay pagos, actualizamos el último o creamos uno nuevo si no existe
                com.jhomilmotors.jhomilwebapp.entity.Payment pagoLocal;
                if (!pagos.isEmpty()) {
                    pagoLocal = pagos.get(pagos.size() - 1); // Tomamos el último
                } else {
                    pagoLocal = new com.jhomilmotors.jhomilwebapp.entity.Payment();
                    pagoLocal.setPedido(pedido);
                    pagoLocal.setMonto(pedido.getTotal());
                    pagoLocal.setMetodo(PaymentMethod.MERCADO_PAGO);
                }

                pagoLocal.setEstado(PaymentStatus.CONFIRMADO);
                pagoLocal.setReferenciaExterna(String.valueOf(mpPayment.getId())); // ID de transacción real
                pagoLocal.setFechaPago(LocalDateTime.now());

                paymentRepository.save(pagoLocal);

                System.out.println("✅ PEDIDO " + codigoPedido + " ACTUALIZADO A PAGADO");
            }

        } catch (Exception e) {
            System.err.println("Error procesando pago MP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}