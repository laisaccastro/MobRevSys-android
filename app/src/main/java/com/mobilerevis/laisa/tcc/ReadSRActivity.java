package com.mobilerevis.laisa.tcc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilerevis.laisa.JWTUtil.JwtToken;
import com.mobilerevis.laisa.Type.IncludeType;
import com.mobilerevis.laisa.Type.StageType;
import com.mobilerevis.laisa.Util;
import com.mobilerevis.laisa.entidades.ReviewedStudy;
import com.mobilerevis.laisa.entidades.ReviewerRole;
import com.mobilerevis.laisa.entidades.Study;
import com.mobilerevis.laisa.entidades.SystematicReview;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadSRActivity extends AppCompatActivity implements ReadSRFragment.OnFragmentInteractionListener {

    FragmentManager fm = getSupportFragmentManager();
    List<Study> studieslist;
    ListView studies;
    ArrayAdapter adapter;
    SystematicReview sr;
    private int selectedStudyindex = 0;
    private MenuItem saveButton, readStudyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (JwtToken.getJWT(ReadSRActivity.this) == null) {
            Intent i = new Intent(ReadSRActivity.this, InicialActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_read_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sr = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                sr = (SystematicReview) extras.getSerializable("systematicReview");
            }
        }
        if (toolbar != null) {
            if (sr.getStage().equals(StageType.INITIAL_SELECTION)) {
                getSupportActionBar().setTitle("Initial study selection");
            } else {
                getSupportActionBar().setTitle("Final study selection");
            }
        }
        if (sr.getStage().equals(StageType.INITIAL_SELECTION)) {
            studieslist = new ArrayList<>();
            for (Study s : sr.getBib().getStudies()) {
                for (ReviewedStudy rs : s.getReviewedStudies()) {
                    if (rs.getReviewer().getEmail().equals(MyApplication.getCurrentUserEmail())) {
                        studieslist.add(s);
                    }
                }
            }
        } else {
            studieslist = new ArrayList<>();
            for (Study s : sr.getBib().getStudies()) {
                for (ReviewedStudy rs : s.getReviewedStudies()) {
                    if (rs.getReviewer().getEmail().equals(MyApplication.getCurrentUserEmail())) {
                        if (rs.getIncludedInitialSelection().equals(IncludeType.INCLUDED)) {
                            studieslist.add(s);
                        }
                    }
                }
            }
        }
        studies = (ListView) findViewById(R.id.listViewStudy);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, studieslist) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                List<ReviewedStudy> reviewedStudies = studieslist.get(position).getReviewedStudies();
                if (position == selectedStudyindex) {
                    tv.setBackgroundColor(Color.LTGRAY);
                } else {
                    tv.setBackgroundColor(Color.WHITE);
                }
                if (reviewedStudies != null) {
                    for (ReviewedStudy rs : reviewedStudies) {
                        if (rs.getReviewer().getEmail().equals(Util.getEmail(getApplicationContext()))) {
                            if (sr.getStage().equals(StageType.INITIAL_SELECTION)) {
                                if (rs.getIncludedInitialSelection() != null) {
                                    switch (rs.getIncludedInitialSelection()) {
                                        case INCLUDED:
                                            tv.setTextColor(Color.GREEN);
                                            break;
                                        case EXCLUDED:
                                            tv.setTextColor(Color.RED);
                                            break;
                                        case DOUBT:
                                            tv.setTextColor(Color.YELLOW);
                                            break;
                                    }
                                }
                            } else if (sr.getStage().equals(StageType.FINAL_SELECTION)) {
                                if (rs.getIncludedFinalSelection() != null) {
                                    switch (rs.getIncludedFinalSelection()) {
                                        case INCLUDED:
                                            tv.setTextColor(Color.GREEN);
                                            break;
                                        case EXCLUDED:
                                            tv.setTextColor(Color.RED);
                                            break;
                                        case DOUBT:
                                            tv.setTextColor(Color.YELLOW);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                return tv;
            }
        };
        studies.setAdapter(adapter);
        studies.setOnItemClickListener(onItemClickListener);
        adapter.notifyDataSetChanged();

        if (savedInstanceState == null) {
            ReadSRFragment readFrag = ReadSRFragment.newInstance(studieslist.get(0), (ArrayList) sr.getCriteria(), sr.getStage());
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.Layout_direito, readFrag, "readFrag");
            ft.commit();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Study s = (Study) studies.getItemAtPosition(position);
            selectedStudyindex = position;
            ReadSRFragment readFrag = (ReadSRFragment) getSupportFragmentManager().findFragmentByTag("readFrag");
            readFrag.updateView(s, (ArrayList) sr.getCriteria(), sr.getStage());
        }
    };

    @Override
    public void onStudyInteraction(Study study) {
        for (Study s : studieslist) {
            if (s.getId() == study.getId()) {
                int index = studieslist.indexOf(s);
                studieslist.set(index, study);
                adapter.notifyDataSetChanged();
            }
        }
        PartialSaveSRTask partialSaveSRTask = new PartialSaveSRTask();
        partialSaveSRTask.execute(sr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_sr, menu);
        saveButton = menu.findItem(R.id.action_save_sr);
        readStudyButton = menu.findItem(R.id.action_read_study);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_sr) {
            SaveSRTask saveSRTask = new SaveSRTask();
            saveSRTask.execute(sr);
            return true;
        }
        if (id == R.id.action_read_study) {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.adobe.reader");
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=" + "com.adobe.reader"));
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class SaveSRTask extends AsyncTask<SystematicReview, Void, Void> {
        @Override
        protected Void doInBackground(SystematicReview... params) {
            SystematicReview srev = params[0];
            try {

                URL url = new URL(BuildConfig.BACKEND_HOST + "/api/systematicreview/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type",
                        "application/json");
                SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);

                conn.setRequestProperty("Authorization", pref.getString("jwt", ""));
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                try {
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
//                    if(srev.getStage().equals(StageType.INITIAL_SELECTION)){
//                        srev.setStage(StageType.FINAL_SELECTION);
//                    }else if(srev.getStage().equals(StageType.FINAL_SELECTION)){
//                        srev.setStage(StageType.FINAL_REVIEW);
//                    }
                    int responseCode;
                    Gson gson = new Gson();
                    String srevJson = gson.toJson(srev);
                    writer.write(srevJson);
                    writer.flush();
                    writer.close();
                    os.close();
                    responseCode = conn.getResponseCode();
                    Log.i("saving", "Response code: " + responseCode);
                } catch (IOException e) {
                    Log.e("saving", "Error sending request to backend.", e);
                }
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
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class PartialSaveSRTask extends AsyncTask<SystematicReview, Void, Void> {
        @Override
        protected Void doInBackground(SystematicReview... params) {
            SystematicReview srev = params[0];
            try {

                URL url = new URL(BuildConfig.BACKEND_HOST + "/api/systematicreview/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type",
                        "application/json");
                SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);

                conn.setRequestProperty("Authorization", pref.getString("jwt", ""));
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                try {
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    int responseCode;
                    Gson gson = new Gson();
                    String srevJson = gson.toJson(srev);
                    writer.write(srevJson);
                    writer.flush();
                    writer.close();
                    os.close();
                    responseCode = conn.getResponseCode();
                    Log.i("saving", "Response code: " + responseCode);
                } catch (IOException e) {
                    Log.e("saving", "Error sending request to backend.", e);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
