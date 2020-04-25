package com.example.sclad.models;

public class Dashboard {
    private int deviceCount;
    private int restockOrderCount;
    private int defectReportCount;


    public Dashboard(int deviceCount, int restockOrderCount, int defectReportCount) {
        this.deviceCount = deviceCount;
        this.restockOrderCount = restockOrderCount;
        this.defectReportCount = defectReportCount;
    }


    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getRestockOrderCount() {
        return restockOrderCount;
    }

    public void setRestockOrderCount(int restockOrderCount) {
        this.restockOrderCount = restockOrderCount;
    }

    public int getDefectReportCount() {
        return defectReportCount;
    }

    public void setDefectReportCount(int defectReportCount) {
        this.defectReportCount = defectReportCount;
    }
}
