package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreateReceiptRequestDTO;
import com.jhomilmotors.jhomilwebapp.dto.ReceiptDTO;
import com.jhomilmotors.jhomilwebapp.entity.Order;
import com.jhomilmotors.jhomilwebapp.entity.Receipt;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptStatus;
import com.jhomilmotors.jhomilwebapp.enums.ReceiptType;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.OrderRepository;
import com.jhomilmotors.jhomilwebapp.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final OrderRepository orderRepository;

    /**
     * Crea un comprobante (Boleta o Factura)
     */
    @Transactional
    public ReceiptDTO createReceipt(CreateReceiptRequestDTO request) {
        Order order = orderRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        try {
            ReceiptType tipo = ReceiptType.fromValue(request.getTipo());

            // Generar número único si no se proporciona
            String numero = request.getNumero() != null ? request.getNumero()
                    : (tipo == ReceiptType.BOLETA ? "B-" : "F-") + System.currentTimeMillis();

            // Verificar que el número no exista
            if (receiptRepository.existsByNumero(numero)) {
                throw new IllegalArgumentException("El número de comprobante ya existe");
            }

            Receipt receipt = Receipt.builder()
                    .pedido(order)
                    .tipo(tipo)
                    .numero(numero)
                    .fechaEmision(LocalDateTime.now())
                    .montoTotal(order.getTotal())
                    .impuesto(order.getImpuestos())
                    .estado(ReceiptStatus.EMITIDA)
                    .build();

            Receipt saved = receiptRepository.save(receipt);
            return convertToDTO(saved);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de comprobante inválido: " + request.getTipo());
        }
    }

    /**
     * Obtiene comprobantes de una orden
     */
    @Transactional(readOnly = true)
    public List<ReceiptDTO> getReceiptsByOrder(Long orderId) {
        return receiptRepository.findByPedidoId(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Anula un comprobante
     */
    @Transactional
    public ReceiptDTO cancelReceipt(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));

        receipt.setEstado(ReceiptStatus.ANULADA);
        Receipt updated = receiptRepository.save(receipt);
        return convertToDTO(updated);
    }

    /**
     * Obtiene todos los comprobantes (admin)
     */
    @Transactional(readOnly = true)
    public Page<ReceiptDTO> getAllReceipts(Pageable pageable) {
        return receiptRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Convierte Receipt a DTO
     */
    private ReceiptDTO convertToDTO(Receipt receipt) {
        return ReceiptDTO.builder()
                .id(receipt.getId())
                .pedidoId(receipt.getPedido().getId())
                .pedidoCodigo(receipt.getPedido().getCodigo())
                .tipo(receipt.getTipo().getValue())
                .numero(receipt.getNumero())
                .fechaEmision(receipt.getFechaEmision())
                .montoTotal(receipt.getMontoTotal())
                .impuesto(receipt.getImpuesto())
                .pdfUrl(receipt.getPdfUrl())
                .estado(receipt.getEstado().getValue())
                .build();
    }

}