package com.b2b.AIhelper.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.b2b.AIhelper.utils.RequestStatus;

public class ServiceRequestBasicDTO {
    private String requestorName;
    private String location;
    private String englishTranslation;
    private String callInfoMalayalam;
    private String issueSummary;
    private LocalDateTime requestDateTime;
    private String audioUrl;
    private RequestStatus status;
	
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
	public String getEnglishTranslation() {
		return englishTranslation;
	}
	public void setEnglishTranslation(String englishTranslation) {
		this.englishTranslation = englishTranslation;
	}
	public String getIssueSummary() {
		return issueSummary;
	}
	public void setIssueSummary(String issueSummary) {
		this.issueSummary = issueSummary;
	}
	public String getCallInfoMalayalam() {
		return callInfoMalayalam;
	}
	public void setCallInfoMalayalam(String callInfoMalayalam) {
		this.callInfoMalayalam = callInfoMalayalam;
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	
}
