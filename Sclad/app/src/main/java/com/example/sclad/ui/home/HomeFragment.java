package com.example.sclad.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.SecurityContextHolder;
import com.example.sclad.Utils.UrlHelper;
import com.example.sclad.models.Dashboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    TextView device, restock, defect;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView txtOne = view.findViewById(R.id.welcomingText);
        txtOne.setText("Welcome, " + SecurityContextHolder.username + "!");

        //I rather see this as async method
        new AsyncTaskLoadDashboardStats().execute();
        return view;
    }

    private class AsyncTaskLoadDashboardStats extends AsyncTask<String, String, Dashboard> {
        @Override
        protected Dashboard doInBackground(String... strings) {
            final String url;
            String json = null;
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(SecurityContextHolder.username,
                            SecurityContextHolder.password))
                    .build();

            url = UrlHelper.resolveApiEndpoint("/api/dashboard/stats");
            Request request = new Request.Builder().url(url).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                json = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int device = 0, restock = 0, faulty = 0;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                device = jsonObject.getInt("deviceCount");
                restock = jsonObject.getInt("restockOrderCount");
                faulty = jsonObject.getInt("defectReportCount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new Dashboard(device, restock, faulty);
        }

        @Override
        protected void onPostExecute(Dashboard dashboard) {
            device = view.findViewById(R.id.deviceLabel);
            restock = view.findViewById(R.id.restockLabel);
            defect = view.findViewById(R.id.defectLabel);
            device.setText(Integer.toString(dashboard.getDeviceCount()));
            restock.setText(Integer.toString(dashboard.getRestockOrderCount()));
            defect.setText(Integer.toString(dashboard.getDefectReportCount()));
        }
    }
}
