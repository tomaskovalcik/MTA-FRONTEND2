package com.example.sclad.ui.faultyDevices;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;
import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.JsonHelper;
import com.example.sclad.Utils.ToastDisplayHelper;
import com.example.sclad.Utils.UrlHelper;
import com.example.sclad.models.FaultReport;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        this.faultReportsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showActionsDialog(getActivity(), "Choose action", faultReports.get(position).getFaultDescription(), faultReports.get(position));
                return false;

            }
        });
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
                Request request = new Request
                        .Builder()
                        .get()
                        .url(UrlHelper.resolveApiEndpoint("/api/uploadedFile/downloadByFileName/" + selectedFaultReport.getAttachment().getFileName()))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastDisplayHelper.displayShortToastMessage("Unspecified server error.", getActivity());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("succ " + response.code());
                    }
                });

                //TODO download attachment

            });
        }
        builder.setNeutralButton("Resolve", (dialog, which) -> {
            //todo delete call to backend
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
