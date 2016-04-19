package com.example.laisa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.laisa.tcc.MyApplication;

public class Util {


    public static void saveEmail(String email, Activity act){
        SharedPreferences preferences = act.getSharedPreferences("mobrevsys", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email",email);
        MyApplication.setCurrentUserEmail(email);
        editor.commit();
    }

    public static String getEmail(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences("mobrevsys", Activity.MODE_PRIVATE);
        MyApplication.setCurrentUserEmail(preferences.getString("email",null));
        return preferences.getString("email",null);
    }
}
