package com.example.sclad.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Device implements Parcelable {


    private String productName;
    private String productCode;
    private Integer quantity;
    private Integer quantityThreshold;
    private Boolean isReordered;

    public Device(String productName, String productCode, Integer quantity, Integer quantityThreshold, Boolean isReordered) {
        this.productName = productName;
        this.productCode = productCode;
        this.quantity = quantity;
        this.quantityThreshold = quantityThreshold;
        this.isReordered = isReordered;
    }

    public Boolean getReordered() {
        return isReordered;
    }

    public void setReordered(Boolean reordered) {
        isReordered = reordered;
    }

    public Integer getQuantityThreshold() {
        return quantityThreshold;
    }

    public void setQuantityThreshold(Integer quantityThreshold) {
        this.quantityThreshold = quantityThreshold;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @NotNull
    @Override
    public String toString() {
        return this.productName + "\tQuanity: " + this.quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
