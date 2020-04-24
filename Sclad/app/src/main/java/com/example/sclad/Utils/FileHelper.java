package com.example.sclad.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import androidx.annotation.RequiresApi;

import java.io.*;

/**
 *  Helper class composed of various methods helping with file uploading.
 *  Inspired by many stack overflow answers.
 */
public class FileHelper {

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri getFilePathFromUri(Context context, Uri uri) throws IOException {
        String fileName = getFileName(context, uri);
        File file = new File(context.getExternalCacheDir(), fileName);
        file.createNewFile();
        try (OutputStream outputStream = new FileOutputStream(file);
             InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            FileUtils.copy(inputStream, outputStream); //Simply reads input to output stream
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    public static String getFileName(Context context, Uri uri) {
        String fileName = getFileNameFromCursor(context, uri);
        if (fileName == null) {
            String fileExtension = getFileExtension(context, uri);
            fileName = "temp_file" + (fileExtension != null ? "." + fileExtension : "");
        } else if (!fileName.contains(".")) {
            String fileExtension = getFileExtension(context, uri);
            fileName = fileName + "." + fileExtension;
        }
        return fileName;
    }

    public static String getFileExtension(Context context, Uri uri) {
        String fileType = context.getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType);
    }

    public static String getFileNameFromCursor(Context context,  Uri uri) {
        Cursor fileCursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        String fileName = null;
        if (fileCursor != null && fileCursor.moveToFirst()) {
            int cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex);
            }
        }
        return fileName;
    }



}
