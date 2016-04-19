package com.example.laisa.tcc;

import android.app.Application;

public class MyApplication extends Application {

    private static String currentUserEmail = null;

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static void setCurrentUserEmail(String newCurrentUserEmail) {
        currentUserEmail = newCurrentUserEmail;
    }
}
