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

import java.util.ArrayList;
import java.util.List;

public class FaultyItemsFragment extends Fragment {



    private ListView lvHomePage;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_faulty_devices, container, false);
        lvHomePage = (ListView) root.findViewById(R.id.frament_slideshow);
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("item1");
        spinnerArray.add("item2");
        Spinner spinner = (Spinner) root.findViewById(R.id.dropdown_category);

        lvHomePage.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1 , spinnerArray));

        return root;
    }
}
