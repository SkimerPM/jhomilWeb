package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.entity.Batch;
import com.jhomilmotors.jhomilwebapp.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

    @GetMapping
    public List<Batch> getAll() {
        return batchService.getAll();
    }

    @GetMapping("/compra/{compraId}")
    public List<Batch> getByCompraId(@PathVariable Long compraId) {
        return batchService.getByCompraId(compraId);
    }

    @GetMapping("/producto/{productoId}")
    public List<Batch> getByProductoId(@PathVariable Long productoId) {
        return batchService.getByProductoId(productoId);
    }

    @GetMapping("/variante/{varianteId}")
    public List<Batch> getByVarianteId(@PathVariable Long varianteId) {
        return batchService.getByVarianteId(varianteId);
    }

    @GetMapping("/proveedor/{proveedorId}")
    public List<Batch> getByProveedorId(@PathVariable Long proveedorId) {
        return batchService.getByProveedorId(proveedorId);
    }

    @GetMapping("/minstock/{cantidad}")
    public List<Batch> getWithMoreThan(@PathVariable int cantidad) {
        return batchService.getWithMoreThanXAvailable(cantidad);
    }

    @GetMapping("/venceantes/{fecha}")
    public List<Batch> getByFechaVencimientoBefore(@PathVariable String fecha) {
        return batchService.getByFechaVencimientoBefore(LocalDate.parse(fecha));
    }

    @GetMapping("/codigo/{codigoLote}")
    public Batch getByCodigoLote(@PathVariable String codigoLote) {
        return batchService.getByCodigoLote(codigoLote).orElse(null);
    }

    @GetMapping("/stock-order-date")
    public List<Batch> getAvailableOrderByDate() {
        return batchService.getAvailableOrderByDate();
    }

    @GetMapping("/stock")
    public List<Batch> getWithAvailableStock() {
        return batchService.getWithAvailableStock();
    }

    @GetMapping("/porvencer/{fecha}")
    public List<Batch> getExpiringBefore(@PathVariable String fecha) {
        return batchService.getExpiringBefore(LocalDate.parse(fecha));
    }

    @GetMapping("/novencidos/{fecha}")
    public List<Batch> getNonExpired(@PathVariable String fecha) {
        return batchService.getNonExpired(LocalDate.parse(fecha));
    }

    @GetMapping("/almacen/{idAlmacen}")
    public List<Batch> getByAlmacen(@PathVariable Integer idAlmacen) {
        return batchService.getByAlmacen(idAlmacen);
    }
}

