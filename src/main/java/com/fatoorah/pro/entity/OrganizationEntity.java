package com.fatoorah.pro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "organizations")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nameAr;

    @Column(unique = true)
    private String vat;

    private String cr;
    private String address;
    private String buildingNo;
    private String postalCode;
    private String city;
    private String invoicePrefix;
    private Double defaultVatRate = 15.0;

    @Column(length = 4000)
    private String privateKeyBase64;

    @Column(length = 4000)
    private String publicKeyBase64;

    @Column(length = 4000)
    private String csidCertificate;

    public OrganizationEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getVat() { return vat; }
    public void setVat(String vat) { this.vat = vat; }

    public String getCr() { return cr; }
    public void setCr(String cr) { this.cr = cr; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getInvoicePrefix() { return invoicePrefix; }
    public void setInvoicePrefix(String invoicePrefix) { this.invoicePrefix = invoicePrefix; }

    public Double getDefaultVatRate() { return defaultVatRate; }
    public void setDefaultVatRate(Double defaultVatRate) { this.defaultVatRate = defaultVatRate; }

    public String getPrivateKeyBase64() { return privateKeyBase64; }
    public void setPrivateKeyBase64(String privateKeyBase64) { this.privateKeyBase64 = privateKeyBase64; }

    public String getPublicKeyBase64() { return publicKeyBase64; }
    public void setPublicKeyBase64(String publicKeyBase64) { this.publicKeyBase64 = publicKeyBase64; }

    public String getCsidCertificate() { return csidCertificate; }
    public void setCsidCertificate(String csidCertificate) { this.csidCertificate = csidCertificate; }
}