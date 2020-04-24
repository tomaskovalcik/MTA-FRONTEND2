package com.example.sclad.Utils;

import org.jetbrains.annotations.NotNull;

public class UrlHelper {

    public static String resolveApiEndpoint(@NotNull String uri) {
        return "10.0.2.2:8080" + uri;
    }
}
