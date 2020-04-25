package com.example.sclad.ui.faultyDevices;

public class FaultReportDisplayItem {

    private Long id;
    private String productName;
    private String deviceSerialNumber;
    private String faultDescription;

    public FaultReportDisplayItem() {
    }

    public FaultReportDisplayItem(Long id, String productName, String deviceSerialNumber, String faultDescription) {
        this.id = id;
        this.productName = productName;
        this.deviceSerialNumber = deviceSerialNumber;
        this.faultDescription = faultDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
