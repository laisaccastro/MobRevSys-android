package com.example.laisa.tcc;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laisa.JWTUtil.JwtToken;
import com.example.laisa.Util;
import com.example.laisa.entidades.Reviewer;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {



    Button btt1;
    EditText edtxt1,edtxt2;
    private static final String TAG = "SignInActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setTitle("Login");
        }

        btt1=(Button)findViewById(R.id.BttLoginReadSR);
        btt1.setOnClickListener(this);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BttLoginReadSR:
                emailSignIn();
                break;


        }
    }

    private void emailSignIn()
    {
        edtxt1=(EditText) findViewById(R.id.EmailLogin);
        String email = edtxt1.getText().toString();
        edtxt2=(EditText) findViewById(R.id.PasswordLogin);
        String password = edtxt2.getText().toString();

        try {
            URL url = new URL(BuildConfig.BACKEND_HOST+"/api/login");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            try {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Reviewer reviewer = new Reviewer();
                reviewer.setEmail(email);
                reviewer.setPassword(password);
                Gson gson = new Gson();
                String reviewerJson = gson.toJson(reviewer);
                writer.write(reviewerJson);
                writer.flush();
                writer.close();
                os.close();
                InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is);
                int responseCode = conn.getResponseCode();
                switch (responseCode){
                    case HttpsURLConnection.HTTP_OK:
                        JwtToken.storeJWT(scanner.next(), LoginActivity.this);
                        Util.saveEmail(email,LoginActivity.this);
                        Intent i = new Intent(LoginActivity.this,ListSRActivity.class);
                        startActivity(i);
                        break;
                    case HttpsURLConnection.HTTP_UNAUTHORIZED:
                        Toast.makeText(LoginActivity.this,"The given password does not match. Please try again.",Toast.LENGTH_SHORT).show();
                        edtxt2.setText("");
                        break;
                    case HttpsURLConnection.HTTP_NOT_FOUND:
                        Toast.makeText(LoginActivity.this,"There is no user with the given email.",Toast.LENGTH_SHORT).show();
                        edtxt1.setText("");
                        edtxt2.setText("");
                        break;
                }
                Log.i(TAG, "Signed in as: " + responseCode);
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




}