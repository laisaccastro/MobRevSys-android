package com.example.laisa;

import android.app.Activity;
import android.content.SharedPreferences;

public class Util {

    public static void saveEmail(String email, Activity act){
        SharedPreferences preferences = act.getSharedPreferences("mobrevsys", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email",email);
    }
}
