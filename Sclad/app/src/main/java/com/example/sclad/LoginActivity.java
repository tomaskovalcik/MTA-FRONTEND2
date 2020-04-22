package com.example.sclad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sclad.Utils.BasicAuthInterceptor;
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Bad Login!")
                            .setMessage("Incorrect Username or Password")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        });
    }

    private void configureLoginBtn() {
        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new BasicAuthInterceptor(usernameInput.getText().toString(),
                                passwordInput.getText().toString()))
                        .build();
                final String url = "http://10.0.2.2:8080/api/user/getCurrentlyLoggedUser";
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.err.println("call with url " + url + " failed.");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            System.out.println("response: " + response.body().string());
                            if (response.code() == 401)
                                showErrorDialog();
                            else
                                startActivity(new Intent(LoginActivity.this,
                                        DashBoardActivity.class).putExtra("USERNAME", usernameInput.getText().toString()));
                        } else {
                            System.err.println("Null response body");
                        }
                    }
                });

            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = usernameInput.getText().toString().trim();
            String passInput = passwordInput.getText().toString().trim();
            loginBtn.setEnabled(!input.isEmpty() && !passInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
