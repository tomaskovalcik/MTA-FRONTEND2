package com.example.sclad.ui.faultyDevices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.FileHelper;
import okhttp3.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static androidx.media.MediaBrowserServiceCompat.RESULT_OK;

public class CreateFaultReportFragment extends Fragment {

    private EditText productNameText;
    private EditText faultDescriptionText;
    private EditText serialNumText;
    private Button uploadFileButton;
    private Button submitFaultReportButton;

    private File selectedFile = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_fault_report, container, false);
        this.productNameText = view.findViewById(R.id.productNameText);
        this.serialNumText = view.findViewById(R.id.serialNumText);
        this.faultDescriptionText = view.findViewById(R.id.faultDescriptionText);
        this.submitFaultReportButton = view.findViewById(R.id.submitFaultReportButton);
        this.uploadFileButton = view.findViewById(R.id.uploadFileButton);
        this.uploadFileButton.setOnClickListener(v -> showFileChooser());
        this.submitFaultReportButton.setOnClickListener(v ->  submit());
        //TODO onSubmit => upload file, return ID, then create faultReport
        return view;
    }

    private static final int FILE_SELECT_CODE = 0;

    private void submit() {
        OkHttpClient client = BasicAuthInterceptor.buildClientWithInterceptor();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileToUpload", this.selectedFile.getName(),
                        RequestBody.create(MediaType.parse(FileHelper.getMimeType(this.selectedFile.getAbsolutePath())), this.selectedFile))
                .build();
        String url = "http://10.0.2.2:8080/api/uploadedFile/create";
        Request postRequest = new Request.Builder().post(requestBody).url(url).build();
        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No connection to the server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    System.out.println("everything ok");
                }
            }
        });
    }
    private void showFileChooser() {
        try {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Select a file to upload");
            startActivityForResult(chooseFile, FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            Uri absoluteFileUri = null;
            try {
                absoluteFileUri = FileHelper.getFilePathFromUri(this.getContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (absoluteFileUri != null && absoluteFileUri.getPath() != null) {
                this.selectedFile = new File(absoluteFileUri.getPath());
            }
        }
    }
}
