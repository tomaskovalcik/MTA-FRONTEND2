package com.example.sclad.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sclad.R;
import com.example.sclad.Utils.SecurityContextHolder;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView txtOne = view.findViewById(R.id.welcomingText);
        txtOne.setText("Welcome, " + SecurityContextHolder.username + "!");
        //TODO Load stats, not necessarily with asyncTasks due to performance issues
        return view;
    }
}
