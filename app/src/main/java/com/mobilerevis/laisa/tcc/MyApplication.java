package com.mobilerevis.laisa.tcc;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    public static SharedPreferences sharedPreferences;

    private static String currentUserEmail = null;

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static void setCurrentUserEmail(String newCurrentUserEmail) {
        currentUserEmail = newCurrentUserEmail;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("mobrevsys", Activity.MODE_PRIVATE);
    }


}
