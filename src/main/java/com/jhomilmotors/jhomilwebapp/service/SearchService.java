package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.SearchResultDTO;
import com.jhomilmotors.jhomilwebapp.entity.Product;
import com.jhomilmotors.jhomilwebapp.entity.ProductVariant;
import com.jhomilmotors.jhomilwebapp.repository.ProductRepository;
import com.jhomilmotors.jhomilwebapp.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ProductVariantRepository variantRepo;

    public List<SearchResultDTO> search(String q) {
        List<SearchResultDTO> results = new ArrayList<>();

        // Productos
        for (Product p : productRepo.buscarTextoProductos(q)) {
            results.add(new SearchResultDTO(
                    "producto",
                    p.getId(),                    // ✅ Long id
                    p.getNombre(),                // ✅ String nombre
                    p.getDescripcion(),           // String descripcion
                    p.getSkuBase(),               // String sku
                    null,                         // Long productoId
                    (p.getBrand() != null ? p.getBrand().getNombre() : null)
            ));
        }

        // Variantes
        for (ProductVariant v : variantRepo.buscarTextoVariantes(q)) {
            results.add(new SearchResultDTO(
                    "variante",
                    v.getId(),
                    v.getProduct().getNombre(),
                    v.getProduct().getDescripcion(),
                    v.getSku(),
                    v.getProduct().getId(),
                    null
            ));
        }

        return results;
    }
}
