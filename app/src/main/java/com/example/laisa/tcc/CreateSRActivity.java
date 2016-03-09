package com.example.laisa.tcc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.laisa.Type.PaperDivisionType;
import com.example.laisa.Type.RoleType;
import com.example.laisa.entidades.BibFile;
import com.example.laisa.entidades.Reviewer;
import com.example.laisa.entidades.ReviewerRole;
import com.example.laisa.entidades.SystematicReview;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CreateSRActivity extends AppCompatActivity {

    EditText edtxt1,edtxt2,edtxt3,edtxt4,edtxt5,edtxt6;
    Button btt1,btt2,btt3,btt4;
    List<String> inclusionCriteria,exclusionCriteria,questions;
    List <ReviewerRole> reviewerRoles;
    ListView inclusionList,exclusionList;
    ArrayAdapter inclusionAdapter,exclusionAdapter;
    final CharSequence[] roles = {"SELECTION","REVIEW","VTM_REVIEW"};
    List<RoleType> selectedRoles;
    private static final String TAG = "CreateActivity";
    private PaperDivisionType divisionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null){
            getSupportActionBar().setTitle("Create Systematic Review");
        }

        edtxt4 = (EditText) findViewById(R.id.CriteriaSR);

        edtxt5 = (EditText) findViewById(R.id.ReviewersSR);


        btt1 = (Button) findViewById(R.id.BttInclusionCriteria);
        btt1.setOnClickListener(inclusionListener);

        btt2 = (Button) findViewById(R.id.BttExclusionCriteria);
        btt2.setOnClickListener(exclusionListener);

        btt3 = (Button) findViewById(R.id.BttInvite);
        btt3.setOnClickListener(inviteListener);

        btt4 = (Button) findViewById(R.id.BttQuestion);
        btt4.setOnClickListener(questionListener);

        inclusionList = (ListView) findViewById(R.id.listViewInclusion);
        inclusionCriteria = new ArrayList<>();
        inclusionAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,inclusionCriteria);
        inclusionList.setAdapter(inclusionAdapter);

        exclusionList = (ListView) findViewById(R.id.listViewExclusion);
        exclusionCriteria = new ArrayList<>();
        exclusionAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,exclusionCriteria);
        exclusionList.setAdapter(exclusionAdapter);

        questions= new ArrayList<>();
        reviewerRoles = new ArrayList<>();




    }

    private View.OnClickListener exclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = edtxt4.getText().toString();
            if(exclusionCriteria.contains(criteria)){
                Toast.makeText(CreateSRActivity.this,"Criteria has already been created",Toast.LENGTH_SHORT).show();

            }else{
                exclusionCriteria.add(criteria);
                exclusionAdapter.notifyDataSetChanged();
            }
        }
    };

    private View.OnClickListener inclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = edtxt4.getText().toString();
            if(inclusionCriteria.contains(criteria)){
                Toast.makeText(CreateSRActivity.this,"Criteria has already been created",Toast.LENGTH_SHORT).show();

            }else{
                inclusionCriteria.add(criteria);
                inclusionAdapter.notifyDataSetChanged();
            }

        }
    };

    private View.OnClickListener inviteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String reviewerEmail = edtxt5.getText().toString();
            boolean exists=false;

            for(ReviewerRole r:reviewerRoles){
                if(r.getReviewer().getEmail().equals(reviewerEmail)){
                    exists=true;
                    break;
                }
            }
            if(exists){
                Toast.makeText(CreateSRActivity.this,"Reviewer has already been added",Toast.LENGTH_SHORT).show();

            }else{
                selectedRoles = new ArrayList();
                final ReviewerRole reviewerRole = new ReviewerRole();
                Reviewer r = new Reviewer();
                r.setEmail(reviewerEmail);
                reviewerRole.setReviewer(r);
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSRActivity.this);
                builder.setMessage(R.string.reviewerAddMessage);
                builder.setMultiChoiceItems(roles, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked)
                        {
                            switch (roles[which].toString()){
                                case "REVIEW":
                                    selectedRoles.add(RoleType.REVIEW);
                                    break;
                                case "SELECTION":
                                    selectedRoles.add(RoleType.SELECTION);
                                    break;
                                case "VTW_REVIEW":
                                    selectedRoles.add(RoleType.VTM_REVIEW);
                                    break;
                            }
                        }
                        else
                        {
                            switch (roles[which].toString()){
                                case "REVIEW":
                                    selectedRoles.remove(RoleType.REVIEW);
                                    break;
                                case "SELECTION":
                                    selectedRoles.remove(RoleType.SELECTION);
                                    break;
                                case "VTW_REVIEW":
                                    selectedRoles.remove(RoleType.VTM_REVIEW);
                                    break;
                            }
                        }
                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedRoles.contains("Selection")){
                            AlertDialog.Builder builderSelection = new AlertDialog.Builder(CreateSRActivity.this);
                            builderSelection.setTitle("Selection Method")
                                    .setMessage("Select the selection method to be used")
                                    .setPositiveButton("Divide studies", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            divisionType = PaperDivisionType.SPLIT;
                                            reviewerRole.setRoles(selectedRoles);
                                            reviewerRoles.add(reviewerRole);
                                        }
                                    })
                                    .setNegativeButton("All studies", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            divisionType = PaperDivisionType.ALL;
                                            reviewerRole.setRoles(selectedRoles);
                                            reviewerRoles.add(reviewerRole);
                                        }
                                    }).create();
                            builderSelection.show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                builder.show();
            }


        }
    };

    private View.OnClickListener questionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            edtxt3 = (EditText) findViewById(R.id.QuestionsSR);
            String question = edtxt3.getText().toString();
            if(questions.contains(question)){
                Toast.makeText(CreateSRActivity.this,"Research question has already been added",Toast.LENGTH_SHORT).show();

            }else{
                questions.add(question);
            }


        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_sr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            createRS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createRS(){
        edtxt1 = (EditText) findViewById(R.id.TitleSR);
        String title = edtxt1.getText().toString();

        edtxt2 = (EditText) findViewById(R.id.ObjectiveSR);
        String objective = edtxt2.getText().toString();


        edtxt6 = (EditText) findViewById(R.id.bibSR);
        String bib = edtxt6.getText().toString();

        try {
            URL url = new URL("http://localhost:8080/api/systematicreview");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type",
                    "application/json");
            SharedPreferences pref = getSharedPreferences("mobrevsys",MODE_PRIVATE);
            conn.setRequestProperty("Authorization",pref.getString("jwt",""));
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            try {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                SystematicReview srev = new SystematicReview();
                BibFile bibFile =new BibFile();
                bibFile.setUrl(new URL(bib));
                srev.setTitle(title);
                srev.setObjective(objective);
                srev.setResearchQuestions(questions);
                srev.setBib(bibFile);
                srev.setParticipatingReviewers(reviewerRoles);
                srev.setDivisionType(divisionType);
                Gson gson = new Gson();
                String srevJson = gson.toJson(srev);
                writer.write(srevJson);
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
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
