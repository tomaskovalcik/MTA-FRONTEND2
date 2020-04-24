package com.example.sclad.Utils;

import android.app.Activity;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

public class ToastDisplayHelper {

    public static void displayShortToastMessage(String message, @NotNull Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show());
    }

    public static void displayLongToastMessage(String message, Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show());
    }

}
