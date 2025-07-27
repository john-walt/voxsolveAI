package com.b2b.AIhelper.service;


import com.b2b.AIhelper.entity.KeralaVisionTroubleshooting;
import com.b2b.AIhelper.repository.KeralaVisionTroubleshootingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TroubleshootingService {

    private final KeralaVisionTroubleshootingRepository repository;

    @Autowired
    public TroubleshootingService(KeralaVisionTroubleshootingRepository repository) {
        this.repository = repository;
    }

    public List<KeralaVisionTroubleshooting> getAllTroubleshootingEntries() {
        return repository.findAll();
    }

    // You might add more specific methods here, e.g., to fetch only active entries, etc.
}
