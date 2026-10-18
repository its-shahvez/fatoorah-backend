package com.fatoorah.pro.repository;

import com.fatoorah.pro.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findAllByOrderByIcvDesc();
    List<InvoiceEntity> findAllByOrderByIcvAsc();
    Optional<InvoiceEntity> findTopByOrderByIcvDesc();
    Optional<InvoiceEntity> findByNumber(String number);
}