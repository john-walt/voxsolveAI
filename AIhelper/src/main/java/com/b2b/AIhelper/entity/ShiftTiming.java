package com.b2b.AIhelper.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class ShiftTiming {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private String shiftFrom;
    private String shiftTo;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and Setters
    

    public Employee getEmployee() {
        return employee;
    }

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getShiftFrom() {
        return shiftFrom;
    }

    public void setShiftFrom(String shiftFrom) {
        this.shiftFrom = shiftFrom;
    }

    public String getShiftTo() {
        return shiftTo;
    }

    public void setShiftTo(String shiftTo) {
        this.shiftTo = shiftTo;
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

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
