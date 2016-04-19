package com.example.laisa.tcc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class InicialActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button btt1,btt2;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignInActivity";
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(JwtToken.getJWT(InicialActivity.this)!=null){
            Util.getEmail(getApplicationContext());
            Intent i = new Intent(InicialActivity.this,ListSRActivity.class);
            startActivity(i);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this.getApplicationContext();

        btt1=(Button) findViewById(R.id.BttLoginInicial);
        btt1.setOnClickListener(loginListener);

        btt2=(Button) findViewById(R.id.BttRegisterInicial);
        btt2.setOnClickListener(registerListener);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.token))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(googleListener);

    }


    public View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(InicialActivity.this,LoginActivity.class);
            startActivity(i);
        }
    };

    public View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in = new Intent(InicialActivity.this,RegisterRActivity.class);
            startActivity(in);
        }
    };

    public View.OnClickListener googleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicial, menu);
        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        try {
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                URL url = new URL(BuildConfig.BACKEND_HOST+"/api/login/token");
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
                    writer.write("idToken=" + URLEncoder.encode(idToken, "UTF-8"));
                    writer.flush();
                    writer.close();
                    os.close();
                    InputStream is = conn.getInputStream();
                    Scanner scan = new Scanner(is);
                    int responseCode = conn.getResponseCode();
                    switch(responseCode){
                        case HttpsURLConnection.HTTP_UNAUTHORIZED:
                            Toast.makeText(InicialActivity.this,"Google SignIn failed!",Toast.LENGTH_SHORT).show();
                            break;
                        case HttpURLConnection.HTTP_CREATED:
                            Intent i = new Intent(getBaseContext(),RegisterRActivity.class);
                            Gson gson = new Gson();
                            Reviewer reviewer = gson.fromJson(scan.next(),Reviewer.class);
                            i.putExtra("email",reviewer.getEmail());
                            i.putExtra("name",reviewer.getName());
                            startActivity(i);
                            break;
                        case HttpURLConnection.HTTP_OK:
                            String jwt = scan.next();
                            JwtToken.storeJWT(jwt,InicialActivity.this);
                            break;
                    }
                    Log.i(TAG, "Signed in as: " + responseCode);
                } catch (IOException e) {
                    Log.e(TAG, "Error sending ID token to backend.", e);
                }


            } else {
                // Signed out, show unauthenticated UI.

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
