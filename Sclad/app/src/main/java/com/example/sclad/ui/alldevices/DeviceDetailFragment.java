package com.example.sclad.ui.alldevices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.models.Device;

public class DeviceDetailFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device_detail, container, false);


        Bundle bundle = getArguments();

        Device device = bundle.getParcelable("device");

        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView productCode = (TextView) view.findViewById(R.id.productCode);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        //TextView isReordered = (TextView) view.findViewById(R.id.isReordered);
        TextView quantityThrashold = (TextView) view.findViewById(R.id.threshold);

        productName.setText(device.getProductName());
        productCode.setText(device.getProductCode());
        quantity.setText(device.getQuantity().toString());
        quantityThrashold.setText(device.getQuantityThreshold().toString());
        //isReordered.setText(device.getReordered().toString());




        return view;
    }
}
