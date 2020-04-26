package com.example.sclad.models;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;

public class Device implements Parcelable {

    private Long id;

    private String productName;

    private String productCode;

    private Integer quantity;

    private Integer quantityThreshold;

    private String deviceType;

    private Boolean reordered;

    public Device(String productName, String productCode, Integer quantity, Integer quantityThreshold, Boolean reordered) {
        this.productName = productName;
        this.productCode = productCode;
        this.quantity = quantity;
        this.quantityThreshold = quantityThreshold;
        this.reordered = reordered;
    }

    public Device() {}

    protected Device(Parcel in) {
        productName = in.readString();
        productCode = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        if (in.readByte() == 0) {
            quantityThreshold = null;
        } else {
            quantityThreshold = in.readInt();
        }
        byte tmpIsReordered = in.readByte();
        reordered = tmpIsReordered == 0 ? null : tmpIsReordered == 1;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public Boolean getReordered() {
        return reordered;
    }

    public void setReordered(Boolean reordered) {
        this.reordered = reordered;
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
        return this.productName + "\t Quanity: " + this.quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productCode);
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        if (quantityThreshold == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantityThreshold);
        }
        dest.writeByte((byte) (reordered == null ? 0 : reordered ? 1 : 2));
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
