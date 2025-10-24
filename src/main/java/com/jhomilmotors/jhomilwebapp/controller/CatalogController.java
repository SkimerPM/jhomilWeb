package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.ProductCatalogResponse;
import com.jhomilmotors.jhomilwebapp.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin; // ¡Importante para React!
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/catalog")
@CrossOrigin(origins = "http://localhost:5173") // Permite al Front-end (React) acceder
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @GetMapping
    public Map<String, Object> getHomePageData() {
        List<ProductCatalogResponse> allProducts = catalogService.findAllCatalogProducts();

        // Simulación para “más vendidos” y “en oferta”
        // (Cambia la lógica cuando tengas ventas reales o promos)
        List<ProductCatalogResponse> bestSellers = allProducts.stream().limit(4).collect(Collectors.toList());
        List<ProductCatalogResponse> offers = allProducts.stream().skip(1).limit(4).collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("productos", allProducts); // Catálogo general
        responseData.put("productosMasVendidos", bestSellers); // Fila especial
        responseData.put("productosEnOferta", offers); // Fila especial
        responseData.put("categorias", catalogService.findAllCategories());
        responseData.put("marcas", catalogService.findAllBrands());
        return responseData;
    }
}