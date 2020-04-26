package com.example.sclad.Utils;

public class SecurityContextHolder {

    public static String username;
    public static String password;

    public static String getCurrentUsersRole() {
        return username.toLowerCase().contains("admin") ? "ROLE_ADMIN" : "ROLE_USER";
    }
}

