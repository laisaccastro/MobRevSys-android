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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mobilerevis.laisa.JWTUtil.JwtToken;
import com.mobilerevis.laisa.Type.StageType;
import com.mobilerevis.laisa.Util;
import com.mobilerevis.laisa.entidades.SystematicReview;
import com.google.gson.Gson;
import com.mobilerevis.laisa.services.MobRevSysBackendService;

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        registerForContextMenu(listSR);
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
        registerForContextMenu(listSR);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewListSR) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(srlist.get(info.position).getTitle());
            String[] menuItems = getResources().getStringArray(R.array.list_sr_context_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.list_sr_context_menu);
        String menuItemName = menuItems[menuItemIndex];
        switch (menuItemName) {
            case "Update":
                Intent i = new Intent(ListSRActivity.this, UpdateSRActivity.class);
                SystematicReview srToUpdate = (SystematicReview) listSR.getItemAtPosition(info.position);
                i.putExtra("systematicReview", srToUpdate);
                startActivity(i);
                break;
            case "Delete":
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.BACKEND_HOST)
                        .build();
                MobRevSysBackendService service = retrofit.create(MobRevSysBackendService.class);
                final SystematicReview srToDelete = (SystematicReview) listSR.getItemAtPosition(info.position);
                SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);
                Call<ResponseBody> response = service.deleteSystematicReview(srToDelete.getId(), pref.getString("jwt", ""));
                response.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        switch (response.code()){
                            case HttpURLConnection.HTTP_OK:
                                srlist.remove(srToDelete);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ListSRActivity.this, "Deleted systematic review", Toast.LENGTH_SHORT).show();
                                break;
                            case HttpURLConnection.HTTP_NOT_FOUND:
                                Toast.makeText(ListSRActivity.this, "Deletion failed, review not found", Toast.LENGTH_SHORT).show();
                                break;
                            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                                Toast.makeText(ListSRActivity.this, "Deletion failed, internal server error", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }

                });
                break;
        }
        return true;
    }

    private void logout() {
        JwtToken.storeJWT(null,ListSRActivity.this);
        Intent i = new Intent(ListSRActivity.this,InicialActivity.class);
        startActivity(i);
        finish();
    }
}
