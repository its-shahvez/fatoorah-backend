package com.fatoorah.pro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;
    private String nameAr;
    private String unit;
    private Double price;
    private String vatCategory;
    private String status = "Active";

    public ProductEntity() {}

    public ProductEntity(String sku, String name, String nameAr, String unit, Double price, String vatCategory, String status) {
        this.sku = sku;
        this.name = name;
        this.nameAr = nameAr;
        this.unit = unit;
        this.price = price;
        this.vatCategory = vatCategory;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getVatCategory() { return vatCategory; }
    public void setVatCategory(String vatCategory) { this.vatCategory = vatCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}