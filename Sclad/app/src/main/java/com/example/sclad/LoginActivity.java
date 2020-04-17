package com.example.sclad;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        configureLoginBtn();
    }

    private void configureLoginBtn() {
        Button loginBtn = findViewById(R.id.login_btn);
        EditText usernameInput = findViewById(R.id.username_input);
        EditText passwordInput = findViewById(R.id.password_input);
        final String username = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new BasicAuthInterceptor(username, password))
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
                            //TODO check if response == 200, if not => display error message
                            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                        } else {
                            System.err.println("Null response body");
                        }
                    }
                });

            }
        });
    }
}
