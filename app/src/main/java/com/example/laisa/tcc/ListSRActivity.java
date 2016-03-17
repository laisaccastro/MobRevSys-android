package com.example.laisa.tcc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.laisa.entidades.SystematicReview;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ListSRActivity extends AppCompatActivity {

    ListView listSR;
    ArrayAdapter adapter;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null){
            getSupportActionBar().setTitle("Systematic Review");
        }

         listSR = (ListView) findViewById(R.id.listViewListSR);

        try {
            URL url = new URL(BuildConfig.BACKEND_HOST+"/api/systematicreview");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type",
                    "application/json");
            SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);
            conn.setRequestProperty("Authorization", pref.getString("jwt", ""));
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is);
            int responseCode = conn.getResponseCode();
            Gson gson = new Gson();
            SystematicReview[] sr =  gson.fromJson(scanner.next(),SystematicReview[].class);
            List<SystematicReview> srlist = Arrays.asList(sr);
            context = getContext();
            adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,srlist);
            listSR.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListSRActivity.this,CreateSRActivity.class);
                startActivity(i);


            }
        });
    }

    public static Context getContext(){
        return context;
    }

}
