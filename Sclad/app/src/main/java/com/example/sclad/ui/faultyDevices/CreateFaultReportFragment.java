package com.example.sclad.ui.faultyDevices;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.DashBoardActivity;
import com.example.sclad.R;
import com.example.sclad.Utils.*;
import com.example.sclad.models.FaultReport;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class CreateFaultReportFragment extends Fragment {

    private EditText productNameText;
    private EditText faultDescriptionText;
    private EditText serialNumText;
    private Button openDateDialog;
    private DatePickerDialog dateOfDiscoveryDialog;
    private Button uploadFileButton;
    private Button submitFaultReportButton;
    private LocalDate selectedDate;

    private File selectedFile = null;
    private static final int FILE_SELECT_CODE = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_fault_report, container, false);
        this.productNameText = view.findViewById(R.id.productNameText);
        this.serialNumText = view.findViewById(R.id.serialNumText);
        this.faultDescriptionText = view.findViewById(R.id.faultDescriptionText);
        this.submitFaultReportButton = view.findViewById(R.id.submitFaultReportButton);
        this.uploadFileButton = view.findViewById(R.id.uploadFileButton);
        this.openDateDialog = view.findViewById(R.id.openDateDialog);
        this.uploadFileButton.setOnClickListener(v -> showFileChooser());
        this.submitFaultReportButton.setOnClickListener(v -> submit());
        this.openDateDialog.setOnClickListener(v -> {
            LocalDate date = LocalDate.now();
            int _day = date.getDayOfMonth();
            int _month = date.getMonthValue() - 1; //compatibility issues with DatePickerDialog interpretation
            int _year = date.getYear();
            dateOfDiscoveryDialog = new DatePickerDialog(getContext(), this::onDateSet, _day, _month, _year);
            dateOfDiscoveryDialog.show();
        });
        //TODO validate inputs, uploaded file presence...
        return view;
    }

    private void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.selectedDate = LocalDate.of(year, month, dayOfMonth);
    }

    private void submit() {
        OkHttpClient client = BasicAuthInterceptor.buildClientWithInterceptor();
        String error;
        if ((error = verifyInput()) != null) {
            ToastDisplayHelper.displayShortToastMessage(error, getActivity());
            return;
        }
        if (selectedFile != null) {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileToUpload", this.selectedFile.getName(),
                            RequestBody.create(MediaType.parse(FileHelper.getMimeType(this.selectedFile.getAbsolutePath())), this.selectedFile))
                    .build();
            String url = "http://10.0.2.2:8080/api/uploadedFile/create";
            Request postRequest = new Request.Builder().post(requestBody).url(url).build();
            client.newCall(postRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastDisplayHelper.displayShortToastMessage("File upload failed.", getActivity());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 200 && response.body() != null) {
                        postFaultReport(client, Long.parseLong(response.body().string()));
                    }
                }
            });
        } else {
            postFaultReport(client, null);
        }
    }

    private String verifyInput() {
        if (this.selectedDate == null || this.selectedDate.compareTo(LocalDate.now()) > 0 || this.selectedDate.getYear() < 1999) {
            return "Wrong date input!";
        }
        if (this.faultDescriptionText.getText().toString().length() < 10 ||
                this.faultDescriptionText.getText().toString().length() > 254) {
            return "Fault description needs to be between 10 and 255 characters!";
        }
        if (this.serialNumText.getText().toString().isEmpty()) {
            return "Serial number field needs to be filled!";
        }
        return null;
    }

    private void postFaultReport(OkHttpClient client, Long attachmentId) {
        FaultReport faultReport = new FaultReport();
        faultReport.setAttachmentId(attachmentId);
        faultReport.setProductName(productNameText.getText().toString());
        faultReport.setDeviceSerialNumber(serialNumText.getText().toString());
        faultReport.setDateOfDiscovery(selectedDate);
        faultReport.setFaultDescription(faultDescriptionText.getText().toString());
        String url = "http://10.0.2.2:8080/api/defectReport/create";
        RequestBody faultReportBody = RequestBody.create(MediaType.parse("application/json"),
                String.valueOf(JsonHelper.toJson(faultReport)));
        Request postRequest = new Request.Builder().post(faultReportBody).url(url).build();
        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastDisplayHelper.displayShortToastMessage("Could not create fault report.", getActivity());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    getActivity().runOnUiThread(() -> {
                        ToastDisplayHelper.displayShortToastMessage(
                                "Created fault report for " + productNameText.getText().toString(), getActivity());
                        startActivity(new Intent(getActivity(),
                                DashBoardActivity.class).putExtra("USERNAME", SecurityContextHolder.username));
                    });
                } else {
                    ToastDisplayHelper.displayShortToastMessage(
                            "Fault report could not be created due to a server error.", getActivity());
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
        if (resultCode == Activity.RESULT_OK) {
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
