package com.fatoorah.pro.config;

import com.fatoorah.pro.entity.*;
import com.fatoorah.pro.repository.*;
import com.fatoorah.pro.service.InvoiceFiscalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceFiscalService invoiceFiscalService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            InvoiceRepository invoiceRepository,
            InvoiceFiscalService invoiceFiscalService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceFiscalService = invoiceFiscalService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Default Users
        if (userRepository.count() == 0) {
            userRepository.save(new UserEntity(
                    null,
                    "shahvez@fatoorah.sa",
                    passwordEncoder.encode("Admin@123"),
                    "Mohd Shahvez",
                    "SUPER_ADMIN",
                    "Active"
            ));
            userRepository.save(new UserEntity(
                    null,
                    "accountant@fatoorah.sa",
                    passwordEncoder.encode("Vat@123"),
                    "Faisal Al-Harbi",
                    "VAT_ACCOUNTANT",
                    "Active"
            ));
        }

        // 2. Ensure Primary Organization & Real secp256k1 Keys Exist
        invoiceFiscalService.getPrimaryOrganization();

        // 3. Seed Saudi B2B Customers
        if (customerRepository.count() == 0) {
            customerRepository.saveAll(List.of(
                    new CustomerEntity("Saudi Aramco Digital Co.", "شركة أرامكو السعودية الرقمية", "300011223300003", "Dhahran", "DHRN4421", "finance@aramcodigital.sa", "+966 13 872 0000", 51750.0),
                    new CustomerEntity("SABIC Industrial Logistics", "سابك للخدمات اللوجستية الصناعية", "300044556600003", "Jubail", "JUBL8812", "ap@sabic.com", "+966 11 225 8000", 28750.0),
                    new CustomerEntity("STC Cloud Infrastructure", "اس تي سي للبنية التحتية السحابية", "300077889900003", "Riyadh", "RRRD2929", "billing@stc.com.sa", "+966 11 455 0000", 19550.0),
                    new CustomerEntity("Almarai Food Processing Co.", "شركة المراعي للصناعات الغذائية", "300099112200003", "Riyadh", "RRRD6150", "procurement@almarai.com", "+966 11 470 0005", 14375.0),
                    new CustomerEntity("Red Sea Global Hospitality", "البحر الأحمر العالمية للضيافة", "300055667700003", "Tabuk", "TBUK1190", "accounts@redseaglobal.com", "+966 11 211 9999", 36800.0),
                    new CustomerEntity("Jarir Bookstore Retail POS", "مكتبة جرير للتسويق", "300033445500003", "Jeddah", "JEDH3301", "vat@jarir.com", "+966 11 462 6000", 4830.0)
            ));
        }

        // 4. Seed Catalog Products
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    new ProductEntity("ERP-ZATCA-ENT", "ZATCA Phase 2 ERP Integration Suite", "حزمة ربط الفوترة الإلكترونية المرحلة الثانية", "PCE", 15000.0, "S", "Active"),
                    new ProductEntity("CLOUD-KSA-NODE", "Dedicated KSA Cloud Server Node (G2)", "خادم سحابي مخصص في المملكة", "MONTH", 4500.0, "S", "Active"),
                    new ProductEntity("SEC-HSM-SIGN", "Hardware Security Module (CSID Signer)", "وحدة حماية التشفير للتوقيع الرقمي", "PCE", 8000.0, "S", "Active"),
                    new ProductEntity("DEV-CONSULT-HR", "Senior Java Microservices Architecture Consulting", "استشارات هندسة البرمجيات المتقدمة", "HUR", 650.0, "S", "Active"),
                    new ProductEntity("POS-TERM-PRO", "Smart Android Thermal POS Terminal + QR", "جهاز نقاط بيع ذكي مع قارئ الباركود", "PCE", 2200.0, "S", "Active"),
                    new ProductEntity("EXP-GCC-SOFT", "GCC Cross-Border Software License Export", "تصدير رخص البرمجيات لدول الخليج", "PCE", 12000.0, "Z", "Active")
            ));
        }

        // 5. Seed Initial Cryptographically Chained Invoices
        if (invoiceRepository.count() == 0) {
            InvoiceEntity inv1 = new InvoiceEntity();
            inv1.setNumber("INV-2026-000145");
            inv1.setType("Standard");
            inv1.setCustomerId("1");
            inv1.setCustomerName("Saudi Aramco Digital Co.");
            inv1.setCustomerNameAr("شركة أرامكو السعودية الرقمية");
            inv1.setCustomerVat("300011223300003");
            inv1.setDate("2026-10-12");
            inv1.setTime("10:15:30");
            inv1.setDueDate("2026-11-12");
            inv1.setStatus("CLEARED");
            inv1.setPaymentTerms("Net 30");
            inv1.setNotes("ZATCA Wave 2 Enterprise Cloud & CSID Deployment");
            inv1.setItems(List.of(
                    new InvoiceItemEntity("1", "ZATCA Phase 2 ERP Integration Suite", 2, 15000.0, 0.0, "S", 4500.0, 34500.0),
                    new InvoiceItemEntity("3", "Hardware Security Module (CSID Signer)", 1, 8000.0, 0.0, "S", 1200.0, 9200.0)
            ));
            invoiceFiscalService.createAndSignInvoice(inv1);

            InvoiceEntity inv2 = new InvoiceEntity();
            inv2.setNumber("INV-2026-000146");
            inv2.setType("Standard");
            inv2.setCustomerId("2");
            inv2.setCustomerName("SABIC Industrial Logistics");
            inv2.setCustomerNameAr("سابك للخدمات اللوجستية الصناعية");
            inv2.setCustomerVat("300044556600003");
            inv2.setDate("2026-10-14");
            inv2.setTime("14:22:10");
            inv2.setDueDate("2026-11-14");
            inv2.setStatus("CLEARED");
            inv2.setPaymentTerms("Net 15");
            inv2.setNotes("KSA Dedicated Cloud Hosting & Microservices Consulting");
            inv2.setItems(List.of(
                    new InvoiceItemEntity("2", "Dedicated KSA Cloud Server Node (G2)", 4, 4500.0, 0.0, "S", 2700.0, 20700.0)
            ));
            invoiceFiscalService.createAndSignInvoice(inv2);

            InvoiceEntity inv3 = new InvoiceEntity();
            inv3.setNumber("INV-2026-000147");
            inv3.setType("Simplified");
            inv3.setCustomerId("6");
            inv3.setCustomerName("Jarir Bookstore Retail POS");
            inv3.setCustomerNameAr("مكتبة جرير للتسويق");
            inv3.setCustomerVat("300033445500003");
            inv3.setDate("2026-10-16");
            inv3.setTime("18:45:00");
            inv3.setDueDate("2026-10-16");
            inv3.setStatus("VALIDATED");
            inv3.setPaymentTerms("Due on Receipt");
            inv3.setNotes("B2C Simplified POS Terminal Order");
            inv3.setItems(List.of(
                    new InvoiceItemEntity("5", "Smart Android Thermal POS Terminal + QR", 2, 2200.0, 0.0, "S", 660.0, 5060.0)
            ));
            invoiceFiscalService.createAndSignInvoice(inv3);
        }
    }
}