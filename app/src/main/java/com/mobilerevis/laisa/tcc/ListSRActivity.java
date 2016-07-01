package com.mobilerevis.laisa.tcc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobilerevis.laisa.JWTUtil.JwtToken;
import com.mobilerevis.laisa.Type.StageType;
import com.mobilerevis.laisa.Util;
import com.mobilerevis.laisa.entidades.SystematicReview;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSRActivity extends AppCompatActivity {

    ListView listSR;
    ArrayAdapter<SystematicReview> adapter;
    private static Context context;
    List<SystematicReview> srlist;
    private MenuItem logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(JwtToken.getJWT(ListSRActivity.this)==null){
            Util.getEmail(getApplicationContext());
            Log.d("pass","hello");
            Intent i = new Intent(ListSRActivity.this,InicialActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_list_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null){
            getSupportActionBar().setTitle("Systematic Review");
        }

        srlist = new ArrayList<>();
        listSR = (ListView) findViewById(R.id.listViewListSR);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,srlist);
        listSR.setAdapter(adapter);
        listSR.setOnItemClickListener(onItemClickListener);
        adapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListSRActivity.this,CreateSRActivity.class);
                startActivity(i);
                ListSRTask listSRTask = new ListSRTask();
                listSRTask.execute();

            }
        });

        ListSRTask listSRTask = new ListSRTask();
        listSRTask.execute();


    }

    @Override
    protected void onResume() {
        super.onResume();
        srlist = new ArrayList<>();
        listSR = (ListView) findViewById(R.id.listViewListSR);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,srlist);
        listSR.setAdapter(adapter);
        listSR.setOnItemClickListener(onItemClickListener);
        adapter.notifyDataSetChanged();
        Util.getEmail(getApplicationContext());
        ListSRTask listSRTask = new ListSRTask();
        listSRTask.execute();

    }

    public static Context getContext(){
        return context;
    }

    private class ListSRTask extends AsyncTask<Void,Void,String> {

        private int responseCode=-1;
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(BuildConfig.BACKEND_HOST+"/api/systematicreview");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type",
                        "application/json");
                SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);
                conn.setRequestProperty("Authorization", pref.getString("jwt", ""));
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                int responseCode = conn.getResponseCode();
                Gson gson = new Gson();
                //TODO O que esta acontecendo com os enums e com os autores???
                SystematicReview[] sr =  gson.fromJson(br,SystematicReview[].class);
                List<SystematicReview> tmpSrList = Arrays.asList(sr);
                srlist.clear();
                for(SystematicReview s:tmpSrList){
                    srlist.add(s);
                }
                context = getContext();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.notifyDataSetChanged();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SystematicReview sr = (SystematicReview) listSR.getItemAtPosition(position);
            if(sr.getStage().equals(StageType.FINAL_REVIEW)){
                new AlertDialog.Builder(ListSRActivity.this)
                        .setTitle("Not yet implemented")
                        .setMessage("We apologize but the review stage hasn't been implemented yet")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }else {
                Intent i = new Intent(ListSRActivity.this, ReadSRActivity.class);
                i.putExtra("systematicReview", sr);
                startActivity(i);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_sr, menu);
        logoutButton = menu.findItem(R.id.action_logout);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        JwtToken.storeJWT(null,ListSRActivity.this);
        Intent i = new Intent(ListSRActivity.this,InicialActivity.class);
        startActivity(i);
        finish();
    }
}
