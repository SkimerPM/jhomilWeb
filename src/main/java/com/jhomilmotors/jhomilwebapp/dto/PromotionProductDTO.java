package com.jhomilmotors.jhomilwebapp.dto;

public class PromotionProductDTO {
    private Long id;
    private Long promotionId;
    private Long productId;
    private Long variantId;
    private Long productGratisId;
    private Long variantGratisId;
    private Integer cantidadRequerida;
    private Integer cantidadGratis;

    public PromotionProductDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public Long getProductGratisId() {
        return productGratisId;
    }

    public void setProductGratisId(Long productGratisId) {
        this.productGratisId = productGratisId;
    }

    public Long getVariantGratisId() {
        return variantGratisId;
    }

    public void setVariantGratisId(Long variantGratisId) {
        this.variantGratisId = variantGratisId;
    }

    public Integer getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(Integer cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }

    public Integer getCantidadGratis() {
        return cantidadGratis;
    }

    public void setCantidadGratis(Integer cantidadGratis) {
        this.cantidadGratis = cantidadGratis;
    }
}
