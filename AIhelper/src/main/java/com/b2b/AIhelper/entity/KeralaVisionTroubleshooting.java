package com.b2b.AIhelper.entity; // Assuming it belongs in the same package or a relevant one

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "keralavision_troubleshooting") // Updated table name to reflect the new entity name
public class KeralaVisionTroubleshooting {

    // Default no-arg constructor is required by JPA
    public KeralaVisionTroubleshooting() {
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "issue_description", nullable = false, length = 1000)
    private String issueDescription;

    @Column(name = "suggested_fix", nullable = false, length = 2000)
    private String suggestedFix;

    // Constructor for creating new instances with essential data
    public KeralaVisionTroubleshooting(String issueDescription, String suggestedFix) {
        this.issueDescription = issueDescription;
        this.suggestedFix = suggestedFix;
        // createdAt and updatedAt will be initialized by their default values
    }

    // You can also add a constructor including timestamps if needed for specific use cases
    public KeralaVisionTroubleshooting(String issueDescription, String suggestedFix, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(issueDescription, suggestedFix); // Call the simpler constructor first
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public void setSuggestedFix(String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void voidSetUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Optional: Override toString() for better logging/debugging
    @Override
    public String toString() {
        return "KeralaVisionTroubleshooting{" +
               "id=" + id +
               ", issueDescription='" + issueDescription + '\'' +
               ", suggestedFix='" + suggestedFix + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}