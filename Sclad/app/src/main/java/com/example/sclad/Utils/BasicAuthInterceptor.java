package com.example.sclad.Utils;

import okhttp3.*;

import java.io.IOException;

public class BasicAuthInterceptor implements Interceptor {

    private final String credentials;

    public BasicAuthInterceptor(String user,  String password) {
        this.credentials = Credentials.basic(user,password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder().header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }

    public static OkHttpClient buildClientWithInterceptor() {
        return new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(SecurityContextHolder.username,
                        SecurityContextHolder.password)).build();
    }
}
