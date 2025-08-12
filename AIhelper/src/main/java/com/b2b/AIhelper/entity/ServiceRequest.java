package com.b2b.AIhelper.entity;

import com.b2b.AIhelper.utils.AllocationStatus;
import com.b2b.AIhelper.utils.Priority;
import com.b2b.AIhelper.utils.RequestStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime requestDateTime; // Date and Time of request

    private String sla;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private String requestorName;

    private String customerWhatsappNumber;

    private String customerDialledNumber;

    @Column(length = 5000) // Large text, but avoid @Lob to prevent oid
    private String callInfoMalayalam;

    @Column(length = 5000)
    private String callInfoEnglish;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Boolean autoTroubleShootingTipsShared;

    @Enumerated(EnumType.STRING)
    private AllocationStatus allocationStatus;

    private String assignedTo;

    @Column(length = 2000)
    private String remarksFromAgent;

    @Column(length = 2000)
    private String remarksFromClient;

    private String attachedImageUrl; // Store image/file URL, not binary

    private String troubleshootingTipsLink;

    private Boolean pendingForApproval;

    @Column(length = 2000)
    private String approvalNotes;
    
    private Boolean verify;
    
    private String audioUrl;
    
    private String location;

    @Column(length = 2000)
    private String issueSummary;
    
    @ManyToMany
    @JoinTable(name = "service_request_address",
            joinColumns = @JoinColumn(name = "service_request_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Set<Address> addresses = new HashSet<>();

    // Getters and Setters
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(LocalDateTime requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	public String getSla() {
		return sla;
	}

	public void setSla(String sla) {
		this.sla = sla;
	}

	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	public String getRequestorName() {
		return requestorName;
	}

	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	public String getCustomerWhatsappNumber() {
		return customerWhatsappNumber;
	}

	public void setCustomerWhatsappNumber(String customerWhatsappNumber) {
		this.customerWhatsappNumber = customerWhatsappNumber;
	}

	public String getCustomerDialledNumber() {
		return customerDialledNumber;
	}

	public void setCustomerDialledNumber(String customerDialledNumber) {
		this.customerDialledNumber = customerDialledNumber;
	}

	public String getCallInfoMalayalam() {
		return callInfoMalayalam;
	}

	public void setCallInfoMalayalam(String callInfoMalayalam) {
		this.callInfoMalayalam = callInfoMalayalam;
	}

	public String getCallInfoEnglish() {
		return callInfoEnglish;
	}

	public void setCallInfoEnglish(String callInfoEnglish) {
		this.callInfoEnglish = callInfoEnglish;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Boolean getAutoTroubleShootingTipsShared() {
		return autoTroubleShootingTipsShared;
	}

	public void setAutoTroubleShootingTipsShared(Boolean autoTroubleShootingTipsShared) {
		this.autoTroubleShootingTipsShared = autoTroubleShootingTipsShared;
	}

	public AllocationStatus getAllocationStatus() {
		return allocationStatus;
	}

	public void setAllocationStatus(AllocationStatus allocationStatus) {
		this.allocationStatus = allocationStatus;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getRemarksFromAgent() {
		return remarksFromAgent;
	}

	public void setRemarksFromAgent(String remarksFromAgent) {
		this.remarksFromAgent = remarksFromAgent;
	}

	public String getRemarksFromClient() {
		return remarksFromClient;
	}

	public void setRemarksFromClient(String remarksFromClient) {
		this.remarksFromClient = remarksFromClient;
	}

	public String getAttachedImageUrl() {
		return attachedImageUrl;
	}

	public void setAttachedImageUrl(String attachedImageUrl) {
		this.attachedImageUrl = attachedImageUrl;
	}

	public String getTroubleshootingTipsLink() {
		return troubleshootingTipsLink;
	}

	public void setTroubleshootingTipsLink(String troubleshootingTipsLink) {
		this.troubleshootingTipsLink = troubleshootingTipsLink;
	}

	public Boolean getPendingForApproval() {
		return pendingForApproval;
	}

	public void setPendingForApproval(Boolean pendingForApproval) {
		this.pendingForApproval = pendingForApproval;
	}

	public String getApprovalNotes() {
		return approvalNotes;
	}

	public void setApprovalNotes(String approvalNotes) {
		this.approvalNotes = approvalNotes;
	}

	public Boolean getVerify() {
		return verify;
	}

	public void setVerify(Boolean verify) {
		this.verify = verify;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIssueSummary() {
		return issueSummary;
	}

	public void setIssueSummary(String issueSummary) {
		this.issueSummary = issueSummary;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}
	
	
}

