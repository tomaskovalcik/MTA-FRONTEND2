package com.example.sclad.ui.faultyDevices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.models.EnumHelper;

import java.util.ArrayList;
import java.util.List;

public class FaultyItemsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faulty_devices, container, false);

        Spinner categoryDropdown = view.findViewById(R.id.dropdown_category);

        List<String> categories = EnumHelper.getDevicesTitleList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, categories);
        categoryDropdown.setAdapter(adapter);

        return view;
    }
}
