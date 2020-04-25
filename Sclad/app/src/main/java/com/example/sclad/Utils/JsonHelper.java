package com.example.sclad.Utils;

import com.example.sclad.models.Device;
import com.example.sclad.models.FaultReport;
import com.example.sclad.models.RestockOrder;
import com.example.sclad.models.UploadedFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    public static JSONObject toJson(RestockOrder restockOrder, Device device) {
        JSONObject object = new JSONObject();
        try {
            object.put("quantityToReorder", restockOrder.getQuantityToReorder());
            object.put("sendNotification", restockOrder.getSendNotification());
            object.put("productName", restockOrder.getProductName());
            object.put("deviceType", restockOrder.getDeviceType().name());
            object.putOpt("device",toJson(device));
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toJson(Device device) {
        JSONObject object = new JSONObject();
        try {
            object.put("id",device.getId());
            object.put("productName", device.getProductName());
            object.put("productCode", device.getProductCode());
            object.put("quantity", device.getQuantity());
            object.put("quantityThreshold", device.getQuantityThreshold());
            object.put("reordered", device.getReordered());
            object.put("deviceType", device.getDeviceType());
            return object;
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toJson(FaultReport faultReport) {
        JSONObject object = new JSONObject();
        try {
            object.put("deviceSerialNumber", faultReport.getDeviceSerialNumber());
            object.put("productName", faultReport.getProductName());
            object.put("dateOfDiscovery",faultReport.getDateOfDiscovery());
            object.put("faultDescription",faultReport.getFaultDescription());
            object.put("attachmentId", faultReport.getAttachmentId());
            object.put("device", null);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Device deviceFromJson(JSONObject deviceJson) {
        Device device = new Device();
        try {
            device.setId(deviceJson.getLong("id"));
            device.setProductCode(deviceJson.getString("productName"));
            device.setProductName(deviceJson.getString("productCode"));
            device.setQuantity(deviceJson.getInt("quantity"));
            device.setQuantityThreshold(deviceJson.getInt("quantityThreshold"));
            device.setDeviceType(deviceJson.getString("deviceType"));
            device.setReordered(deviceJson.getBoolean("reordered"));
            return device;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<FaultReport> faultReportListFromJson(JSONArray jsonArray) {
        List<FaultReport> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                FaultReport faultReport = new FaultReport();
                JSONObject object = jsonArray.getJSONObject(i);
                faultReport.setFaultDescription(object.getString("faultDescription"));
                faultReport.setDateOfDiscovery(LocalDate.parse(object.getString("dateOfDiscovery")));
                faultReport.setDeviceSerialNumber(object.getString("deviceSerialNumber"));
                JSONObject device = object.getJSONObject("device");
                faultReport.setDevice(deviceFromJson(device));
                faultReport.setProductName(device.getString("productName"));
                if (!JSONObject.NULL.equals(object.get("attachment"))) {
                    JSONObject uploadedFileJson = object.getJSONObject("attachment");
                    UploadedFile uploadedFile = new UploadedFile();
                    uploadedFile.setFileName(uploadedFileJson.getString("fileName"));
                    uploadedFile.setFileType(uploadedFileJson.getString("fileType"));
                    uploadedFile.setId(uploadedFileJson.getLong("id"));
                    faultReport.setAttachmentId(uploadedFileJson.getLong("id"));
                    faultReport.setAttachment(uploadedFile);
                }
                list.add(faultReport);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
