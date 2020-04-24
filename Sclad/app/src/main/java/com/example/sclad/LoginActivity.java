package com.example.sclad;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.sclad.Utils.BasicAuthInterceptor;
import com.example.sclad.Utils.SecurityContextHolder;
import com.example.sclad.Utils.ToastDisplayHelper;
import com.example.sclad.Utils.UrlHelper;
import okhttp3.*;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        configureLoginBtn();
        //Initialize username/password text fields, disable login button by default
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        usernameInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setEnabled(false);
    }

    public void showErrorDialog() {
        runOnUiThread(() -> {
            if (!isFinishing()) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Bad Login!")
                        .setMessage("Incorrect Username or Password")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                        }).show();
            }
        });
    }

    private void configureLoginBtn() {
        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(v -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(usernameInput.getText().toString(),
                            passwordInput.getText().toString()))
                    .build();
            Request request = new Request.Builder().url(UrlHelper.resolveApiEndpoint("/api/user/getCurrentlyLoggedUser")).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastDisplayHelper.displayShortToastMessage("Unspecified server error.", getParent());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if (response.body() != null) {
                        if (response.code() != 200) {
                            showErrorDialog();
                        } else {
                            SecurityContextHolder.username = usernameInput.getText().toString();
                            SecurityContextHolder.password = passwordInput.getText().toString();
                            startActivity(new Intent(LoginActivity.this,
                                    DashBoardActivity.class));
                        }
                    }
                }
            });

        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = usernameInput.getText().toString().trim();
            String passInput = passwordInput.getText().toString().trim();
            loginBtn.setEnabled(!input.isEmpty() && !passInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}
