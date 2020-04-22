package com.example.sclad.ui.alldevices;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.models.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListDevicesFragment extends Fragment {

    ArrayList<Device> items = new ArrayList<Device>();
    ListView listView;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_all_devices, container, false);
        new AsyncTaskLoadListOfDevices().execute();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskLoadListOfDevices extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json = null;
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor("admin",
                            "admin"))
                    .build();
            final String url = "http://10.0.2.2:8080/api/device/all";
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
                    isReordered = json_data.getBoolean("isReordered");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                items.add(new Device(productName, productCode, quantity, quantityThreshold, isReordered));

            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            listView = (ListView) view.findViewById(R.id.listview_stored_items);
            ArrayAdapter<Device> listViewAdapter = new ArrayAdapter<Device>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    items
            );

            listView.setAdapter(listViewAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Device device = (Device) parent.getAdapter().getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device", device);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    DeviceDetailFragment deviceDetailFragment = new DeviceDetailFragment();
                    deviceDetailFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.nav_host_fragment, deviceDetailFragment);
                    fragmentTransaction.commit();

                }
            });
        }
    }
}
