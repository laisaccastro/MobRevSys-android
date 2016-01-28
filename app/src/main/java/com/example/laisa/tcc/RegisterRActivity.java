package com.example.laisa.tcc;

import android.graphics.Color;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class RegisterRActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
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
            register();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void register() {
        edtxt1 = (EditText) findViewById(R.id.NameRegister);
        String name = edtxt1.getText().toString();

        edtxt2 = (EditText) findViewById(R.id.EmailRegister);
        String email = edtxt2.getText().toString();

        edtxt3 = (EditText) findViewById(R.id.PasswordRegister);
        String password = edtxt3.getText().toString();

        edtxt4 = (EditText) findViewById(R.id.CPasswordRegister);
        String cpassword = edtxt4.getText().toString();

        edtxt5 = (EditText) findViewById(R.id.UniAffiation);
        String uniaffiation = edtxt5.getText().toString();

        edtxt6 = (EditText) findViewById(R.id.CountryRegister);
        String country = edtxt6.getText().toString();

        if (password.equals(cpassword)) {
            try {
                URL url = new URL("http://localhost:8080/api/register");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
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
                    JSONObject reviewer = new JSONObject();
                    reviewer.put("name", name);
                    reviewer.put("email", email);
                    reviewer.put("password", password);
                    reviewer.put("uniaffiation", uniaffiation);
                    reviewer.put("country",country);
                    writer.write(reviewer.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    Log.i(TAG, "Signed in as: " + responseCode);
                } catch (IOException e) {
                    Log.e(TAG, "Error sending ID token to backend.", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            edtxt3.setText("");
            edtxt3.setBackgroundColor(0xFFFF0000);
            edtxt4.setText("");
            edtxt4.setBackgroundColor(0xFFFF0000);
            Toast.makeText(RegisterRActivity.this,"The given password does not match the password confirmation!",Toast.LENGTH_SHORT).show();
        }
    }





    }



