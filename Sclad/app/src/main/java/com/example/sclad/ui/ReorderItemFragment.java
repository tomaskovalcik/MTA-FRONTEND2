package com.example.sclad.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.example.sclad.R;
import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.JsonHelper;
import com.example.sclad.Utils.SecurityContextHolder;
import com.example.sclad.models.Device;
import com.example.sclad.models.DeviceType;
import com.example.sclad.models.EnumHelper;
import com.example.sclad.models.RestockOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ReorderItemFragment extends Fragment {

    private EditText productNameInput;
    private Spinner categoryDropdown;
    private SeekBar quantitySeekBar;
    private Switch sendNotificationSwitch;
    private TextView quantityTextView;
    private Button submitBtn;

    public ReorderItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reorder_item, container, false);
        Spinner categoryDropdown = view.findViewById(R.id.categoryDropdown);

        List<String> categories = EnumHelper.getDevicesTitleList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        categoryDropdown.setAdapter(adapter);

        this.productNameInput = view.findViewById(R.id.productNameInput);
        this.categoryDropdown = view.findViewById(R.id.categoryDropdown);
        this.quantitySeekBar = view.findViewById(R.id.quantitySeekBar);
        this.sendNotificationSwitch = view.findViewById(R.id.sendNotificationSwitch);
        this.submitBtn = view.findViewById(R.id.submitBtn);
        this.quantityTextView = view.findViewById(R.id.quantityTextView);
        this.quantityTextView.setText(Integer.toString(this.quantitySeekBar.getProgress()));

        quantitySeekBar.setOnSeekBarChangeListener(onChangeListener);
        submitBtn.setOnClickListener(onClickListener);
        return view;
    }

    public View.OnClickListener onClickListener = event -> {
        //Get input from fields in the form
        String productName = productNameInput.getText().toString();
        Boolean sendNotification = sendNotificationSwitch.isChecked();
        Integer quantity = quantitySeekBar.getProgress();
        String category = categoryDropdown.getSelectedItem().toString();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(SecurityContextHolder.username,
                        SecurityContextHolder.password)).build();
        final String url = "http://10.0.2.2:8080/api/device/getDeviceByProductName/" + productName;
        ObjectMapper mapper = new ObjectMapper();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //nothing
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    if (responseBody != null && response.code() == 200) {
                        Device device = mapper.readValue(responseBody, Device.class);
                        RestockOrder restockOrder = new RestockOrder();
                        restockOrder.setDevice(device);
                        restockOrder.setProductName(device.getProductName());
                        restockOrder.setQuantityToReorder(quantity);
                        restockOrder.setSendNotification(sendNotification);
                        restockOrder.setDeviceType(DeviceType.valueOf(device.getDeviceType()));
                        JSONObject restockOrderJson = JsonHelper.toJson(restockOrder, device);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), String.valueOf(restockOrderJson));
                        String url = "http://10.0.2.2:8080/api/restockOrder/create";
                        Request postRequest = new Request.Builder().post(body).url(url).build();
                        client.newCall(postRequest).execute();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast t = Toast.makeText(getContext(), "Created restock order for device.", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast t = Toast.makeText(getContext(), "Unknown device.", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    };

    public android.widget.SeekBar.OnSeekBarChangeListener onChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int value = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            value = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //not needed
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            quantityTextView = Objects.requireNonNull(getView()).findViewById(R.id.quantityTextView);
            quantityTextView.setText(Integer.toString(value));
        }
    };
}
