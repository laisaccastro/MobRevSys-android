package com.example.laisa.tcc;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laisa.JWTUtil.JwtToken;
import com.example.laisa.entidades.Reviewer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class RegisterRActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int REGISTER_RESULT = 9002;
    EditText edtxt1,edtxt2,edtxt3,edtxt4,edtxt5,edtxt6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_r);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null){
            getSupportActionBar().setTitle("Register Reviewer");
        }
        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        edtxt1 = (EditText) findViewById(R.id.NameRegister);
        edtxt2 = (EditText) findViewById(R.id.EmailRegister);
        if(email!=null&&name!=null){
            edtxt1.setText(name);
            edtxt2.setText(email);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_r, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_register) {
            String name = edtxt1.getText().toString();


            String email = edtxt2.getText().toString();

            edtxt3 = (EditText) findViewById(R.id.PasswordRegister);
            String password = edtxt3.getText().toString();

            edtxt4 = (EditText) findViewById(R.id.CPasswordRegister);
            String cpassword = edtxt4.getText().toString();

            edtxt5 = (EditText) findViewById(R.id.UniAffiation);
            String uniaffiliation = edtxt5.getText().toString();

            edtxt6 = (EditText) findViewById(R.id.CountryRegister);
            String country = edtxt6.getText().toString();
            Map<String,String> map = new HashMap<>();
            map.put("name",name);
            map.put("email",email);
            map.put("password",password);
            map.put("cpassword",cpassword);
            map.put("uniaffiliation",uniaffiliation);
            map.put("country",country);
            LoginTask task = new LoginTask();
            task.execute(map);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginTask extends AsyncTask<Map<String,String>,Void,String>{

        private int responseCode =-1;
        @Override
        protected String doInBackground(Map<String,String>... params) {
            Map<String,String> map = params[0];
            String name = map.get("name");
            String email = map.get("email");
            String password = map.get("password");
            String cpassword = map.get("cpassword");
            String uniaffiation = map.get("uniaffiliation");
            String country = map.get("country");
            if (password.equals(cpassword)) {
                try {
                    URL url = new URL(BuildConfig.BACKEND_HOST+"/api/register");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestProperty("Content-Type",
                            "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    try {
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        Reviewer reviewer = new Reviewer();
                        reviewer.setName(name);
                        reviewer.setEmail(email);
                        reviewer.setPassword(password);
                        reviewer.setAffiliatedUniversity(uniaffiation);
                        reviewer.setCountry(country);
                        Gson gson = new Gson();
                        String reviewerJson = gson.toJson(reviewer);
                        writer.write(reviewerJson);
                        writer.flush();
                        writer.close();
                        os.close();
                        Scanner scanner = null;
                        responseCode = conn.getResponseCode();
                        Log.i(TAG, "Response code: " + responseCode);
                        if(responseCode<400){
                            InputStream is = conn.getInputStream();
                            scanner = new Scanner(is);

                        }else{
                            InputStream is = conn.getErrorStream();
                            scanner = new Scanner(is);
                        }
                        return scanner.next();

                    } catch (IOException e) {
                        Log.e(TAG, "Error sending ID token to backend.", e);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String jwt) {
            switch (responseCode) {
                case HttpsURLConnection.HTTP_OK:
                    JwtToken.storeJWT(jwt,RegisterRActivity.this);
                    Intent i = new Intent(RegisterRActivity.this, ListSRActivity.class);
                    Log.d(TAG,jwt);
                    startActivity(i);

                    return;
                case HttpsURLConnection.HTTP_NOT_ACCEPTABLE:
                    Toast.makeText(RegisterRActivity.this, "an user with the given email already exists.", Toast.LENGTH_SHORT).show();
                    edtxt2.setText("");
                    edtxt3.setText("");
                    edtxt4.setText("");
                    return;
            }
            edtxt3.setText("");
            edtxt3.setBackgroundColor(0xFFFF0000);
            edtxt4.setText("");
            edtxt4.setBackgroundColor(0xFFFF0000);
            Toast.makeText(RegisterRActivity.this,"The given password does not match the password confirmation!",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"");

            return;
        }
    }






    }



