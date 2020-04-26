package com.example.sclad.ui.faultyDevices;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.sclad.R;
import com.example.sclad.Utils.*;
import com.example.sclad.models.FaultReport;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FaultReportsFragment extends Fragment {

    private ListView faultReportsList;
    private ArrayList<FaultReport> faultReports;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fault_reports, container, false);
        this.faultReportsList = view.findViewById(R.id.fault_reports_list);
        populateList();
        this.faultReportsList.setOnItemLongClickListener((parent, view1, position, id) -> {
            showActionsDialog(getActivity(), "Choose action", faultReports.get(position).getFaultDescription(), faultReports.get(position));
            return false;
        });
        this.faultReportsList.setOnItemClickListener(((parent, view1, position, id) -> ToastDisplayHelper.displayShortToastMessage("Long press to see fault report actions", getActivity())));
        return view;
    }

    private void showActionsDialog(Activity activity, String title, CharSequence message, FaultReport selectedFaultReport) {
        OkHttpClient client = BasicAuthInterceptor.buildClientWithInterceptor();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        if (selectedFaultReport.getAttachmentId() != null) {

            builder.setPositiveButton("Download attachment", (dialog, which) -> {
                if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

                    // this will request for permission when user has not granted permission for the app
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
                } else {
                    Request getRequest = new Request
                            .Builder()
                            .get()
                            .url(UrlHelper.resolveApiEndpoint("/api/uploadedFile/downloadByFileName/" + selectedFaultReport.getAttachment().getFileName()))
                            .build();
                    client.newCall(getRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ToastDisplayHelper.displayShortToastMessage("Unspecified server error while downloading attachment.", getActivity());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == 200 && response.body() != null) {
                                File downloadedFile = new File(getContext().getCacheDir(), selectedFaultReport.getAttachment().getFileName());
                                BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                                sink.writeAll(response.body().source());
                                sink.flush();
                                sink.close();
                                ToastDisplayHelper.displayShortToastMessage("Downloaded attachment.", getActivity());
                            } else {
                                ToastDisplayHelper.displayShortToastMessage("Error while downloading attachment.", getActivity());
                            }
                        }
                    });
                }
            });
        }
        builder.setNeutralButton("Resolve", (dialog, which) -> {
            if (SecurityContextHolder.getCurrentUsersRole().equals("ROLE_ADMIN")) {
                Request request = new Request
                        .Builder()
                        .delete()
                        .url(UrlHelper.resolveApiEndpoint("/api/defectReport/resolve/" + selectedFaultReport.getId()))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastDisplayHelper.displayShortToastMessage("Unspecified server error.", getActivity());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.code() == 200) {
                            faultReports.remove(selectedFaultReport);
                            ToastDisplayHelper.displayShortToastMessage("Fault report " + selectedFaultReport.getId() + " successfully resolved.", getActivity());
                            getActivity().runOnUiThread(() -> {
                                ((BaseAdapter) faultReportsList.getAdapter()).notifyDataSetChanged();
                                faultReportsList.invalidateViews();
                            });
                        } else {
                            ToastDisplayHelper.displayShortToastMessage("Could not resolve fault report.", getActivity());
                        }
                    }
                });
            } else {
                ToastDisplayHelper.displayShortToastMessage("Only administrator can perform this action!", getActivity());
            }
        });

        builder.show();
    }

    private void populateList() {
        OkHttpClient client = BasicAuthInterceptor.buildClientWithInterceptor();
        Request request = new Request.Builder().get().url(UrlHelper.resolveApiEndpoint("/api/defectReport/list")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastDisplayHelper.displayShortToastMessage("Could not load fault reports", getActivity());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        faultReports = (ArrayList<FaultReport>) JsonHelper.faultReportListFromJson(jsonArray);
                        getActivity().runOnUiThread(() -> {
                            faultReportsList = getView().findViewById(R.id.fault_reports_list);
                            ArrayAdapter<FaultReport> faultReportAdapter = new ArrayAdapter<>(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    faultReports
                            );
                            faultReportsList.setAdapter(faultReportAdapter);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
