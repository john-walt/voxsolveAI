package com.b2b.AIhelper.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.b2b.AIhelper.utils.RequestStatus;

public class ServiceRequestBasicDTO {
	private String category;
    private String requestorName;
    private String location;
    private String issueDescription;
    private LocalDateTime requestDateTime;
    private RequestStatus status;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getRequestorName() {
		return requestorName;
	}
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIssueDescription() {
		return issueDescription;
	}
	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}
	public LocalDateTime getRequestDateTime() {
		return requestDateTime;
	}
	public void setRequestDateTime(LocalDateTime requestDateTime) {
		this.requestDateTime = requestDateTime;
	}
	public RequestStatus getStatus() {
		return status;
	}
	public void setStatus(RequestStatus status) {
		this.status = status;
	}

}
