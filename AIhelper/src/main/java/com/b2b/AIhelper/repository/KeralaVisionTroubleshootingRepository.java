package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.KeralaVisionTroubleshooting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KeralaVisionTroubleshootingRepository extends JpaRepository<KeralaVisionTroubleshooting, UUID> {
    // Spring Data JPA will automatically provide basic CRUD operations
}