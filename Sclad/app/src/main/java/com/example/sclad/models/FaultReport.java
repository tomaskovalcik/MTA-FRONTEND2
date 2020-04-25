package com.example.sclad.models;

import java.time.LocalDate;

public class FaultReport {

    private Long id;

    private String productName;

    private String deviceSerialNumber;

    private String faultDescription;

    private LocalDate dateOfDiscovery;

    private Long attachmentId;

    private Device device;

    private UploadedFile attachment;

    public FaultReport() {
    }

    @Override
    public String toString() {
        return
                productName + ", " +
                        "Serial number: " + deviceSerialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public LocalDate getDateOfDiscovery() {
        return dateOfDiscovery;
    }

    public void setDateOfDiscovery(LocalDate dateOfDiscovery) {
        this.dateOfDiscovery = dateOfDiscovery;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UploadedFile getAttachment() {
        return attachment;
    }

    public void setAttachment(UploadedFile attachment) {
        this.attachment = attachment;
    }
}
