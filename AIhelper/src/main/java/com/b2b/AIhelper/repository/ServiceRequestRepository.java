package com.b2b.AIhelper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.b2b.AIhelper.entity.ServiceRequest;
import com.b2b.AIhelper.utils.RequestStatus;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByStatus(RequestStatus status);
}
