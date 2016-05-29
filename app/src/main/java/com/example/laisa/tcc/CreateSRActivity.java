package com.example.laisa.tcc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.laisa.JWTUtil.JwtToken;
import com.example.laisa.Type.CriteriaType;
import com.example.laisa.Type.PaperDivisionType;
import com.example.laisa.Type.RoleType;
import com.example.laisa.Type.StageType;
import com.example.laisa.entidades.BibFile;
import com.example.laisa.entidades.Criteria;
import com.example.laisa.entidades.Reviewer;
import com.example.laisa.entidades.ReviewerRole;
import com.example.laisa.entidades.SystematicReview;
import com.google.gson.Gson;
import com.nononsenseapps.filepicker.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class CreateSRActivity extends AppCompatActivity {

    private final int FILE_CODE = 3234;

    EditText edtxt1, edtxt2, edtxt3, edtxt4, edtxt5;
    Button btt1, btt2, btt3, btt4, bttBib, bttObj;
    List<String> inclusionCriteria, exclusionCriteria, questions, objectives;
    List<ReviewerRole> reviewerRoles;
    ListView inclusionList, exclusionList;
    ArrayAdapter inclusionAdapter, exclusionAdapter;
//    final CharSequence[] roles = new CharSequence[]{"SELECTION", "REVIEW", "VTM_REVIEW"};
final CharSequence[] roles = new CharSequence[]{"SELECTION", "REVIEW"};
    final boolean[] checkedRoles = new boolean[]{false, false, false};
    List<RoleType> selectedRoles;
    private static final String TAG = "CreateActivity";
    private PaperDivisionType divisionType;
    private boolean readyToCreate = false;
    private MenuItem createButton;
    private Uri bibUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (JwtToken.getJWT(CreateSRActivity.this) == null) {
            Intent i = new Intent(CreateSRActivity.this, InicialActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_create_sr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setTitle("Create Systematic Review");
        }
        edtxt2 = (EditText) findViewById(R.id.ObjectiveSR);

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

        bttBib = (Button) findViewById(R.id.bibSR);
        bttBib.setOnClickListener(bibBtListener);

        bttObj = (Button) findViewById(R.id.BttObjective);
        bttObj.setOnClickListener(objectiveListener);

        inclusionList = (ListView) findViewById(R.id.listViewInclusion);
        inclusionCriteria = new ArrayList<>();
        inclusionAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, inclusionCriteria);
        inclusionList.setAdapter(inclusionAdapter);

        exclusionList = (ListView) findViewById(R.id.listViewExclusion);
        exclusionCriteria = new ArrayList<>();
        exclusionAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, exclusionCriteria);
        exclusionList.setAdapter(exclusionAdapter);

        questions = new ArrayList<>();
        reviewerRoles = new ArrayList<>();
        objectives = new ArrayList<>();


    }

    private View.OnClickListener objectiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            edtxt2 = (EditText) findViewById(R.id.ObjectiveSR);
            String objective = edtxt2.getText().toString();
            if(objective.equals("")){
                Toast.makeText(CreateSRActivity.this, "Objective can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (objectives.contains(objective)) {
                Toast.makeText(CreateSRActivity.this, "Objective has already been added", Toast.LENGTH_SHORT).show();

            } else {
                objectives.add(objective);
                edtxt2.setText("");
                Toast.makeText(CreateSRActivity.this, "Objective added", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private View.OnClickListener exclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = edtxt4.getText().toString();
            if(criteria.equals("")){
                Toast.makeText(CreateSRActivity.this, "Criteria can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            edtxt4.setText("");
            if (exclusionCriteria.contains(criteria)) {
                Toast.makeText(CreateSRActivity.this, "Criteria has already been created", Toast.LENGTH_SHORT).show();

            } else {
                exclusionCriteria.add(criteria);
                exclusionAdapter.notifyDataSetChanged();
                Toast.makeText(CreateSRActivity.this, "Criteria created", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private View.OnClickListener inclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = edtxt4.getText().toString();
            if(criteria.equals("")){
                Toast.makeText(CreateSRActivity.this, "Criteria can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            edtxt4.setText("");
            if (inclusionCriteria.contains(criteria)) {
                Toast.makeText(CreateSRActivity.this, "Criteria has already been created", Toast.LENGTH_SHORT).show();

            } else {
                inclusionCriteria.add(criteria);
                inclusionAdapter.notifyDataSetChanged();
                Toast.makeText(CreateSRActivity.this, "Criteria created", Toast.LENGTH_SHORT).show();

            }

        }
    };

    private View.OnClickListener inviteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String reviewerEmail = edtxt5.getText().toString();
            if(reviewerEmail.equals("")){
                Toast.makeText(CreateSRActivity.this, "Reviewer email can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            edtxt5.setText("");
            boolean exists = false;

            for (ReviewerRole r : reviewerRoles) {
                if (r.getReviewer().getEmail().equals(reviewerEmail)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                Toast.makeText(CreateSRActivity.this, "Reviewer has already been added", Toast.LENGTH_SHORT).show();

            } else {
                selectedRoles = new ArrayList();
                final ReviewerRole reviewerRole = new ReviewerRole();
                Reviewer r = new Reviewer();
                r.setEmail(reviewerEmail);
                reviewerRole.setReviewer(r);
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateSRActivity.this);
                builder.setMultiChoiceItems(roles, checkedRoles, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            switch (roles[which].toString()) {
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
                        } else {
                            switch (roles[which].toString()) {
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
                        //TODO Tipo de divisao deve ser escolhido apenas uma vez por revisão, Radio Button??
                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedRoles.contains(RoleType.SELECTION)) {
                            AlertDialog.Builder builderSelection = new AlertDialog.Builder(CreateSRActivity.this);
                            builderSelection.setTitle("Selection Method")
                                    .setMessage("Select the selection activity to be used")
                                    .setPositiveButton("Divide equally between the reviewers", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            divisionType = PaperDivisionType.SPLIT;
                                            reviewerRole.setRoles(selectedRoles);
                                            reviewerRoles.add(reviewerRole);
                                            Toast.makeText(CreateSRActivity.this,"Invitation sent",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("Everyone gets all studies", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            divisionType = PaperDivisionType.ALL;
                                            reviewerRole.setRoles(selectedRoles);
                                            reviewerRoles.add(reviewerRole);
                                            Toast.makeText(CreateSRActivity.this,"Invitation sent",Toast.LENGTH_SHORT).show();
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
            if(question.equals("")){
                Toast.makeText(CreateSRActivity.this, "Question can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (questions.contains(question)) {
                Toast.makeText(CreateSRActivity.this, "Research question has already been added", Toast.LENGTH_SHORT).show();

            } else {
                questions.add(question);
                edtxt3.setText("");
                Toast.makeText(CreateSRActivity.this, "Research question added", Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_sr, menu);
        createButton = menu.findItem(R.id.action_create);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        createButton.setEnabled(readyToCreate);
        super.onPrepareOptionsMenu(menu);
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

    public void createRS() {
        edtxt1 = (EditText) findViewById(R.id.TitleSR);
        String title = edtxt1.getText().toString();
        if(title.equals("")){
            Toast.makeText(CreateSRActivity.this, "Title can't be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Criteria> criterias = new ArrayList<>();
        for(String s: inclusionCriteria){
            Criteria c = new Criteria(s, CriteriaType.INCLUSION);
            criterias.add(c);
        }
        for(String s:  exclusionCriteria){
            Criteria c = new Criteria(s,CriteriaType.EXCLUSION);
            criterias.add(c);
        }
        SystematicReview srev = new SystematicReview();
        srev.setTitle(title);
        srev.setObjectives(objectives);
        srev.setResearchQuestions(questions);
        srev.setCriteria(criterias);
        srev.setParticipatingReviewers(reviewerRoles);
        srev.setDivisionType(divisionType);
        srev.setStage(StageType.INITIAL_SELECTION);
        CreateSRTask createSRTask = new CreateSRTask();
        createSRTask.execute(srev);

    }

    private class CreateSRTask extends AsyncTask<SystematicReview, Void, Void> {
        @Override
        protected Void doInBackground(SystematicReview... params) {
            SystematicReview srev = params[0];
            try {
                URL urlbib = new URL(BuildConfig.BACKEND_HOST + "/api/bib");
                HttpURLConnection connbib = (HttpURLConnection) urlbib.openConnection();
                connbib.setReadTimeout(10000);
                connbib.setConnectTimeout(15000);
                connbib.setRequestProperty("Content-Type",
                        "application/octet-stream");
                SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);
                connbib.setRequestProperty("Authorization", pref.getString("jwt", ""));
                connbib.setRequestMethod("POST");

                connbib.setDoInput(true);
                connbib.setDoOutput(true);

                OutputStream osbib = connbib.getOutputStream();
                osbib.write(getBytes(bibUri));
                osbib.flush();
                osbib.close();
                int responseCode;
                InputStream is = connbib.getInputStream();
                Gson gson = new Gson();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                BibFile bib = gson.fromJson(br, BibFile.class);
                srev.setBib(bib);


                URL url = new URL(BuildConfig.BACKEND_HOST + "/api/systematicreview");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestProperty("Content-Type",
                        "application/json");
                conn.setRequestProperty("Authorization", pref.getString("jwt", ""));
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                try {
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    String srevJson = gson.toJson(srev);
                    writer.write(srevJson);
                    writer.flush();
                    writer.close();
                    os.close();
                    responseCode = conn.getResponseCode();
                    Log.i(TAG, "Response code: " + responseCode);
                } catch (IOException e) {
                    Log.e(TAG, "Error sending request to backend.", e);
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

    private View.OnClickListener bibBtListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(CreateSRActivity.this, FilePickerActivity.class);
            i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
            startActivityForResult(i, FILE_CODE);
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            saveUri(uri);
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            saveUri(uri);
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                saveUri(uri);
            }
        }
    }

    private void saveUri(Uri uri) {
        bibUri = uri;
        readyToCreate = true;
        invalidateOptionsMenu();
        Toast.makeText(CreateSRActivity.this,".bib successfully selected",Toast.LENGTH_SHORT).show();
    }

    public byte[] getBytes(Uri uri) throws IOException {

        InputStream is = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
