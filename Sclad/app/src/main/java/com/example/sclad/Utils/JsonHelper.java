package com.example.sclad.Utils;

import com.example.sclad.models.Device;
import com.example.sclad.models.RestockOrder;
import org.json.JSONException;
import org.json.JSONObject;

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
}
