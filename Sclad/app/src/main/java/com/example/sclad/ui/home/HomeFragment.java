package com.example.sclad.ui.home;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sclad.DashBoardActivity;
import com.example.sclad.R;
import com.example.sclad.Utils.SecurityContextHolder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    EditText username;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView txtOne = (TextView) view.findViewById(R.id.welcomingText);
        txtOne.setText("Welcome " + SecurityContextHolder.username);

        return view;
    }


    private class AsyncTaskLoadListOfDevices extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
