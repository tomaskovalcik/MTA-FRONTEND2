package com.example.sclad.ui.alldevices;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sclad.R;
import com.example.sclad.models.Device;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_detail);
        Bundle bundle = getIntent().getExtras();

        Device device = bundle.getParcelable("device");

        TextView productName = findViewById(R.id.productName);
        TextView productCode = findViewById(R.id.productCode);
        TextView quantity = findViewById(R.id.quantity);
        //TextView isReordered = (TextView) view.findViewById(R.id.isReordered);
        TextView quantityThrashold = findViewById(R.id.threshold);

        productName.setText(device.getProductName());
        productCode.setText(device.getProductCode());
        quantity.setText(device.getQuantity().toString());
        quantityThrashold.setText(device.getQuantityThreshold().toString());


    }

}
