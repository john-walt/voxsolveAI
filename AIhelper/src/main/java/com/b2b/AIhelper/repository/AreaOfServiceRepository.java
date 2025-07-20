package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.AreaOfService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AreaOfServiceRepository extends JpaRepository<AreaOfService, Long> {
    Optional<AreaOfService> findByAreaName(String areaName); // Fetch area by name
}
