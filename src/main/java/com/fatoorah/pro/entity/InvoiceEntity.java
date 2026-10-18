package com.fatoorah.pro.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number;
    private String type;

    private String customerId;
    private String customerName;
    private String customerNameAr;
    private String customerVat;

    private String date;
    private String time;
    private String dueDate;

    private Double subtotal;
    private Double discountTotal;
    private Double taxableAmount;
    private Double vatAmount;
    private Double total;

    private String status;
    private String notes;
    private String paymentTerms;

    private Long icv;
    private String uuid;

    @Column(length = 1000)
    private String previousHash;

    @Column(length = 1000)
    private String xmlHash;

    @Column(length = 1500)
    private String digitalSignature;

    @Column(length = 3000)
    private String qrTlvBase64;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceItemEntity> items;

    public InvoiceEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerNameAr() { return customerNameAr; }
    public void setCustomerNameAr(String customerNameAr) { this.customerNameAr = customerNameAr; }

    public String getCustomerVat() { return customerVat; }
    public void setCustomerVat(String customerVat) { this.customerVat = customerVat; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getDiscountTotal() { return discountTotal; }
    public void setDiscountTotal(Double discountTotal) { this.discountTotal = discountTotal; }

    public Double getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(Double taxableAmount) { this.taxableAmount = taxableAmount; }

    public Double getVatAmount() { return vatAmount; }
    public void setVatAmount(Double vatAmount) { this.vatAmount = vatAmount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public Long getIcv() { return icv; }
    public void setIcv(Long icv) { this.icv = icv; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

    public String getXmlHash() { return xmlHash; }
    public void setXmlHash(String xmlHash) { this.xmlHash = xmlHash; }

    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }

    public String getQrTlvBase64() { return qrTlvBase64; }
    public void setQrTlvBase64(String qrTlvBase64) { this.qrTlvBase64 = qrTlvBase64; }

    public List<InvoiceItemEntity> getItems() { return items; }
    public void setItems(List<InvoiceItemEntity> items) { this.items = items; }
}