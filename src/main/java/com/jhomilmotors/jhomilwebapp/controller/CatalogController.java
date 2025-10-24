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

    /**
     * Endpoint unificado que devuelve todos los datos necesarios para la página de inicio/catálogo.
     */
    @GetMapping
    public Map<String, Object> getHomePageData() {

        List<ProductCatalogResponse> allProducts = catalogService.findAllCatalogProducts();

        // Lógica de filtrado de ofertas y más vendidos (ejemplo simple)
        List<ProductCatalogResponse> productsInOffer = allProducts.stream()
                .limit(4) // Simulación: tomamos los 4 primeros
                .collect(Collectors.toList());

        List<ProductCatalogResponse> mostSold = allProducts.stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .limit(4)
                .collect(Collectors.toList());

        // Empaquetar la respuesta JSON
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("productos", allProducts);
        responseData.put("productosEnOferta", productsInOffer);
        responseData.put("productosMasVendidos", mostSold);
        responseData.put("categorias", catalogService.findAllCategories());
        responseData.put("marcas", catalogService.findAllBrands());

        return responseData;
    }
}