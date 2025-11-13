package com.jhomilmotors.jhomilwebapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class SyncCartRequestDTO {
    private List<SyncCartItemDTO> items;

    @Data
    public static class SyncCartItemDTO {
        private Long productoId;
        private Long variantId;
        private Integer cantidad;
    }
}