package com.b2b.AIhelper.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.b2b.AIhelper.entity.ServiceRequest;
import com.b2b.AIhelper.utils.RequestStatus;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Page<ServiceRequest> findByStatus(RequestStatus status, Pageable pageable);
}
