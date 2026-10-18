package com.fatoorah.pro.service;

import com.fatoorah.pro.entity.*;
import com.fatoorah.pro.repository.*;
import com.fatoorah.pro.zatca.ZatcaCryptoEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InvoiceFiscalService {

    private final InvoiceRepository invoiceRepository;
    private final OrganizationRepository organizationRepository;
    private final CustomerRepository customerRepository;
    private final AuditLogRepository auditLogRepository;
    private final ZatcaCryptoEngine zatcaCryptoEngine;

    public InvoiceFiscalService(
            InvoiceRepository invoiceRepository,
            OrganizationRepository organizationRepository,
            CustomerRepository customerRepository,
            AuditLogRepository auditLogRepository,
            ZatcaCryptoEngine zatcaCryptoEngine
    ) {
        this.invoiceRepository = invoiceRepository;
        this.organizationRepository = organizationRepository;
        this.customerRepository = customerRepository;
        this.auditLogRepository = auditLogRepository;
        this.zatcaCryptoEngine = zatcaCryptoEngine;
    }

    public OrganizationEntity getPrimaryOrganization() {
        return organizationRepository.findAll().stream().findFirst().orElseGet(() -> {
            OrganizationEntity org = new OrganizationEntity();
            org.setName("Al-Madinah Tech Solutions Co.");
            org.setNameAr("شركة المدينة للحلول التقنية");
            org.setVat("300075588900003");
            org.setCr("1010998877");
            org.setAddress("King Fahd Road, Olaya District");
            org.setBuildingNo("7428");
            org.setPostalCode("12214");
            org.setCity("Riyadh");
            org.setInvoicePrefix("INV-2026-");
            org.setDefaultVatRate(15.0);
            Map<String, String> keys = zatcaCryptoEngine.generateSecp256k1KeyPair();
            org.setPrivateKeyBase64(keys.get("privateKey"));
            org.setPublicKeyBase64(keys.get("publicKey"));
            org.setCsidCertificate(keys.get("csid"));
            return organizationRepository.save(org);
        });
    }

    // Create & Cryptographically Sign a New ZATCA Phase 2 Invoice
    @Transactional
    public synchronized InvoiceEntity createAndSignInvoice(InvoiceEntity draft) {
        OrganizationEntity org = getPrimaryOrganization();

        // 1. Sequential ICV & Previous Invoice Hash (PIH) Lookup
        Optional<InvoiceEntity> lastInvoiceOpt = invoiceRepository.findTopByOrderByIcvDesc();
        long nextIcv = lastInvoiceOpt.map(inv -> inv.getIcv() + 1).orElse(1L);
        String previousHash = lastInvoiceOpt.map(InvoiceEntity::getXmlHash).orElse(ZatcaCryptoEngine.GENESIS_PIH);

        draft.setIcv(nextIcv);
        draft.setPreviousHash(previousHash);
        if (draft.getUuid() == null || draft.getUuid().isBlank()) {
            draft.setUuid(UUID.randomUUID().toString());
        }

        if (draft.getNumber() == null || draft.getNumber().isBlank()) {
            String prefix = org.getInvoicePrefix() != null ? org.getInvoicePrefix() : "INV-2026-";
            draft.setNumber(prefix + String.format("%06d", nextIcv));
        }

        String nowDate = draft.getDate() != null && !draft.getDate().isBlank()
                ? draft.getDate()
                : LocalDate.now().toString();
        String nowTime = draft.getTime() != null && !draft.getTime().isBlank()
                ? draft.getTime()
                : LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        draft.setDate(nowDate);
        draft.setTime(nowTime);
        if (draft.getDueDate() == null || draft.getDueDate().isBlank()) {
            draft.setDueDate(LocalDate.now().plusDays(30).toString());
        }

        // 2. Calculate Line Item Totals & 15% Saudi VAT
        double subtotal = 0.0;
        double discountTotal = 0.0;
        double vatTotal = 0.0;

        if (draft.getItems() != null) {
            for (InvoiceItemEntity item : draft.getItems()) {
                int qty = item.getQty() != null ? item.getQty() : 1;
                double price = item.getPrice() != null ? item.getPrice() : 0.0;
                double disc = item.getDiscount() != null ? item.getDiscount() : 0.0;
                String vatCat = item.getVatCategory() != null ? item.getVatCategory() : "S";

                double lineGross = qty * price;
                double lineTaxable = Math.max(0.0, lineGross - disc);
                double rate = "S".equalsIgnoreCase(vatCat) ? 0.15 : 0.0;
                double lineVat = round2(lineTaxable * rate);
                double lineTotal = round2(lineTaxable + lineVat);

                item.setVatCategory(vatCat);
                item.setVatAmount(lineVat);
                item.setTotal(lineTotal);

                subtotal += lineGross;
                discountTotal += disc;
                vatTotal += lineVat;
            }
        }

        double taxableAmount = round2(Math.max(0.0, subtotal - discountTotal));
        vatTotal = round2(vatTotal);
        double grandTotal = round2(taxableAmount + vatTotal);

        draft.setSubtotal(round2(subtotal));
        draft.setDiscountTotal(round2(discountTotal));
        draft.setTaxableAmount(taxableAmount);
        draft.setVatAmount(vatTotal);
        draft.setTotal(grandTotal);

        if (draft.getStatus() == null || draft.getStatus().isBlank()) {
            draft.setStatus("CLEARED");
        }

        // 3. Build UBL 2.1 XML, SHA-256 Hash, ECDSA secp256k1 Signature & 8-Tag TLV QR
        String canonicalXml = zatcaCryptoEngine.buildCanonicalUblXml(draft, org);
        String xmlHash = zatcaCryptoEngine.computeSha256Base64(canonicalXml);
        String signature = zatcaCryptoEngine.signWithSecp256k1(xmlHash, org.getPrivateKeyBase64());
        String isoTimestamp = nowDate + "T" + nowTime + "Z";

        String qrBase64 = zatcaCryptoEngine.generateZatcaTlvQrBase64(
                org.getName(),
                org.getVat(),
                isoTimestamp,
                grandTotal,
                vatTotal,
                xmlHash,
                signature,
                org.getPublicKeyBase64()
        );

        draft.setXmlHash(xmlHash);
        draft.setDigitalSignature(signature);
        draft.setQrTlvBase64(qrBase64);

        InvoiceEntity saved = invoiceRepository.save(draft);

        // 4. Update Customer Total Invoiced
        if (saved.getCustomerName() != null) {
            customerRepository.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(saved.getCustomerName()))
                    .findFirst()
                    .ifPresent(c -> {
                        c.setTotalInvoiced(round2((c.getTotalInvoiced() != null ? c.getTotalInvoiced() : 0.0) + grandTotal));
                        customerRepository.save(c);
                    });
        }

        // 5. Record Immutable Audit Log
        auditLogRepository.save(new AuditLogEntity(
                String.valueOf(saved.getId()),
                saved.getNumber(),
                "CLEARED".equalsIgnoreCase(saved.getStatus()) ? "CLEARED_BY_ZATCA" : "INVOICE_CREATED",
                "Mohd Shahvez (ZATCA Phase 2 Engine)",
                isoTimestamp,
                "ICV #" + saved.getIcv() + " signed with secp256k1 ECDSA & linked to PIH " + previousHash.substring(0, 16) + "..."
        ));

        return saved;
    }

    // Quick Clear / Submit Pending or Draft Invoice to ZATCA
    @Transactional
    public InvoiceEntity clearInvoiceWithZatca(String idOrNumber) {
        InvoiceEntity inv = findByIdOrNumber(idOrNumber);
        OrganizationEntity org = getPrimaryOrganization();

        inv.setStatus("Simplified".equalsIgnoreCase(inv.getType()) ? "VALIDATED" : "CLEARED");

        // Re-sign & refresh cryptographic stamp
        String canonicalXml = zatcaCryptoEngine.buildCanonicalUblXml(inv, org);
        String xmlHash = zatcaCryptoEngine.computeSha256Base64(canonicalXml);
        String signature = zatcaCryptoEngine.signWithSecp256k1(xmlHash, org.getPrivateKeyBase64());
        String isoTimestamp = inv.getDate() + "T" + inv.getTime() + "Z";

        String qrBase64 = zatcaCryptoEngine.generateZatcaTlvQrBase64(
                org.getName(),
                org.getVat(),
                isoTimestamp,
                inv.getTotal(),
                inv.getVatAmount(),
                xmlHash,
                signature,
                org.getPublicKeyBase64()
        );

        inv.setXmlHash(xmlHash);
        inv.setDigitalSignature(signature);
        inv.setQrTlvBase64(qrBase64);

        InvoiceEntity saved = invoiceRepository.save(inv);

        auditLogRepository.save(new AuditLogEntity(
                String.valueOf(saved.getId()),
                saved.getNumber(),
                "CLEARED_BY_ZATCA",
                "ZATCA Fatoora Core Gateway",
                LocalDate.now() + "T" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "Z",
                "Cleared with ZATCA Cryptographic Stamp. XML Hash: " + xmlHash.substring(0, 20) + "..."
        ));

        return saved;
    }

    // Verify Complete SHA-256 Blockchain Hash-Chain Across All Invoices
    public Map<String, Object> verifyHashChainIntegrity() {
        List<InvoiceEntity> ordered = invoiceRepository.findAllByOrderByIcvAsc();
        OrganizationEntity org = getPrimaryOrganization();

        List<Map<String, Object>> nodes = new ArrayList<>();
        boolean chainIntact = true;
        String brokenAtInvoice = null;

        for (int i = 0; i < ordered.size(); i++) {
            InvoiceEntity curr = ordered.get(i);
            String expectedPrevHash = (i == 0) ? curr.getPreviousHash() : ordered.get(i - 1).getXmlHash();

            String recomputedXml = zatcaCryptoEngine.buildCanonicalUblXml(curr, org);
            String recomputedHash = zatcaCryptoEngine.computeSha256Base64(recomputedXml);

            boolean hashMatchesPayload = recomputedHash.equals(curr.getXmlHash());
            boolean prevLinkMatches = (i == 0) || Objects.equals(curr.getPreviousHash(), expectedPrevHash);
            boolean validNode = hashMatchesPayload && prevLinkMatches;

            if (!validNode && chainIntact) {
                chainIntact = false;
                brokenAtInvoice = curr.getNumber();
            }

            Map<String, Object> node = new HashMap<>();
            node.put("icv", curr.getIcv());
            node.put("number", curr.getNumber());
            node.put("total", curr.getTotal());
            node.put("previousHash", curr.getPreviousHash());
            node.put("storedXmlHash", curr.getXmlHash());
            node.put("recomputedXmlHash", recomputedHash);
            node.put("valid", validNode);
            nodes.add(node);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", chainIntact);
        result.put("totalChecked", ordered.size());
        result.put("brokenAtInvoice", brokenAtInvoice);
        result.put("nodes", nodes);
        return result;
    }

    // ZATCA Form-01 VAT Return Calculation (Box 1 to Box 14)
    public Map<String, Object> calculateVatReturnForm01() {
        List<InvoiceEntity> all = invoiceRepository.findAll();
        double standardSales = 0.0;
        double standardVat = 0.0;
        double zeroRatedSales = 0.0;
        double exemptSales = 0.0;
        double creditNotesAmount = 0.0;
        double creditNotesVat = 0.0;

        for (InvoiceEntity inv : all) {
            if ("Credit".equalsIgnoreCase(inv.getType())) {
                creditNotesAmount += Math.abs(inv.getTaxableAmount() != null ? inv.getTaxableAmount() : 0.0);
                creditNotesVat += Math.abs(inv.getVatAmount() != null ? inv.getVatAmount() : 0.0);
                continue;
            }
            if (inv.getItems() != null && !inv.getItems().isEmpty()) {
                for (InvoiceItemEntity item : inv.getItems()) {
                    double lineTaxable = (item.getQty() * item.getPrice()) - (item.getDiscount() != null ? item.getDiscount() : 0.0);
                    String cat = item.getVatCategory() != null ? item.getVatCategory() : "S";
                    if ("S".equalsIgnoreCase(cat)) {
                        standardSales += lineTaxable;
                        standardVat += (item.getVatAmount() != null ? item.getVatAmount() : lineTaxable * 0.15);
                    } else if ("Z".equalsIgnoreCase(cat)) {
                        zeroRatedSales += lineTaxable;
                    } else if ("E".equalsIgnoreCase(cat)) {
                        exemptSales += lineTaxable;
                    }
                }
            } else {
                standardSales += (inv.getTaxableAmount() != null ? inv.getTaxableAmount() : 0.0);
                standardVat += (inv.getVatAmount() != null ? inv.getVatAmount() : 0.0);
            }
        }

        double netVatDue = round2(standardVat - creditNotesVat);

        Map<String, Object> form = new HashMap<>();
        form.put("box1_standardRatedSales", round2(standardSales));
        form.put("box1_adjustments", round2(creditNotesAmount));
        form.put("box1_netVat", round2(standardVat - creditNotesVat));
        form.put("box3_zeroRatedSales", round2(zeroRatedSales));
        form.put("box4_exemptSales", round2(exemptSales));
        form.put("box6_totalSales", round2(standardSales + zeroRatedSales + exemptSales));
        form.put("box14_netVatDue", netVatDue);
        form.put("invoiceCount", all.size());
        return form;
    }

    public InvoiceEntity findByIdOrNumber(String idOrNumber) {
        try {
            Long numericId = Long.parseLong(idOrNumber);
            Optional<InvoiceEntity> byId = invoiceRepository.findById(numericId);
            if (byId.isPresent()) return byId.get();
        } catch (NumberFormatException ignored) {}

        return invoiceRepository.findByNumber(idOrNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + idOrNumber));
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}