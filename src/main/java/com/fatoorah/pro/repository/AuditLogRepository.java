package com.fatoorah.pro.repository;

import com.fatoorah.pro.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findAllByOrderByIdDesc();
    List<AuditLogEntity> findByInvoiceNumberOrderByIdDesc(String invoiceNumber);
}