package com.fatoorah.pro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_items")
public class InvoiceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;
    private Integer qty;
    private Double price;
    private Double discount;
    private String vatCategory;
    private Double vatAmount;
    private Double total;

    public InvoiceItemEntity() {}

    public InvoiceItemEntity(String productId, String name, Integer qty, Double price, Double discount, String vatCategory, Double vatAmount, Double total) {
        this.productId = productId;
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.discount = discount;
        this.vatCategory = vatCategory;
        this.vatAmount = vatAmount;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getVatCategory() { return vatCategory; }
    public void setVatCategory(String vatCategory) { this.vatCategory = vatCategory; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}