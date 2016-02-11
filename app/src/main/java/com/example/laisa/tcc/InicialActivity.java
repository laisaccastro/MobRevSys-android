package com.example.laisa.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class InicialActivity extends AppCompatActivity {

    Button btt1,btt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = new Intent(InicialActivity.this,LoginActivity.class);
        startActivity(i);

        btt1=(Button) findViewById(R.id.BttLoginInicial);
        btt1.setOnClickListener(loginListener);

        btt2=(Button) findViewById(R.id.BttRegisterInicial);
        btt2.setOnClickListener(registerListener);

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
}
