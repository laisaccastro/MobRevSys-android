package com.example.laisa.JWTUtil;

import android.app.Activity;
import android.content.SharedPreferences;

public class JwtToken {
    public static void storeJWT(String jwt, Activity act){
        SharedPreferences preferences = act.getSharedPreferences("mobrevsys", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("jwt",jwt);

    }
}
