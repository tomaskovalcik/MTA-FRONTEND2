package com.example.sclad.ui.alldevices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.UrlHelper;
import com.example.sclad.models.Device;
import com.example.sclad.models.EnumHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListDevicesFragment extends Fragment {

    ArrayList<Device> items = new ArrayList<>();
    ListView listView;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_all_devices, container, false);
        Spinner categoryDropdown = view.findViewById(R.id.categoryList);

        List<String> categories = EnumHelper.getDevicesTitleList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        categoryDropdown.setAdapter(adapter);
        categories.add(0, "all");

        categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                items.clear();
                String category = categoryDropdown.getSelectedItem().toString();
                new AsyncTaskLoadListOfDevices().execute(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskLoadListOfDevices extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String category = strings[0];
            final String url;
            String json = null;
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor("admin",
                            "admin"))
                    .build();
            if (category.equals("all"))
                url = UrlHelper.resolveApiEndpoint("/api/device/all");
            else
                url = UrlHelper.resolveApiEndpoint("api/device/listAllDevicesByType/" + category);
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
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject json_data = null;
            int id, quantity = 0, quantityThreshold = 0;
            String productName = null, productCode = null;
            Boolean isReordered = null;

            for (int i = 0; i < jsonarray.length(); i++) {
                try {
                    json_data = jsonarray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    id = json_data.getInt("id");
                    productName = json_data.getString("productName");
                    productCode = json_data.getString("productCode");
                    quantity = json_data.getInt("quantity");
                    quantityThreshold = json_data.getInt("quantityThreshold");
                    isReordered = json_data.optBoolean("isReordered");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                items.add(new Device(productName, productCode, quantity, quantityThreshold, isReordered));
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            listView = view.findViewById(R.id.listview_stored_items);
            ArrayAdapter<Device> listViewAdapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    items
            );
            listView.setAdapter(listViewAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Device device = (Device) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", device);

                startActivity(new Intent(getActivity(),
                        DetailActivity.class).putExtras(bundle));

            });
        }
    }
}
