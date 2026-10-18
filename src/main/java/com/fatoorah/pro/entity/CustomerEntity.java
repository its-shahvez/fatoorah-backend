package com.fatoorah.pro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nameAr;
    private String vat;
    private String city;
    private String shortAddress;
    private String email;
    private String phone;
    private Double totalInvoiced = 0.0;

    public CustomerEntity() {}

    public CustomerEntity(String name, String nameAr, String vat, String city, String shortAddress, String email, String phone, Double totalInvoiced) {
        this.name = name;
        this.nameAr = nameAr;
        this.vat = vat;
        this.city = city;
        this.shortAddress = shortAddress;
        this.email = email;
        this.phone = phone;
        this.totalInvoiced = totalInvoiced;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getVat() { return vat; }
    public void setVat(String vat) { this.vat = vat; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getShortAddress() { return shortAddress; }
    public void setShortAddress(String shortAddress) { this.shortAddress = shortAddress; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getTotalInvoiced() { return totalInvoiced; }
    public void setTotalInvoiced(Double totalInvoiced) { this.totalInvoiced = totalInvoiced; }
}