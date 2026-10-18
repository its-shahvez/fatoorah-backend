package com.fatoorah.pro.repository;

import com.fatoorah.pro.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {
}