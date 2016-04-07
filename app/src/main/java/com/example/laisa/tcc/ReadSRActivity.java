package com.example.laisa.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.laisa.JWTUtil.JwtToken;

public class ReadSRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(JwtToken.getJWT(ReadSRActivity.this)==null){
            Intent i = new Intent(ReadSRActivity.this,InicialActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_read_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
