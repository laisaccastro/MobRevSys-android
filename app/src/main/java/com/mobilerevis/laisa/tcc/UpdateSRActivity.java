package com.mobilerevis.laisa.tcc;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mobilerevis.laisa.Type.CriteriaType;
import com.mobilerevis.laisa.entidades.Criteria;
import com.mobilerevis.laisa.entidades.ReviewedStudy;
import com.mobilerevis.laisa.entidades.ReviewedStudyCriteria;
import com.mobilerevis.laisa.entidades.Study;
import com.mobilerevis.laisa.entidades.SystematicReview;
import com.mobilerevis.laisa.services.MobRevSysBackendService;
import com.mobilerevis.laisa.tcc.databinding.ActivityUpdateSrBinding;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateSRActivity extends AppCompatActivity {

    ActivityUpdateSrBinding binding;
    SystematicReview sr;
    List<Criteria> inclusionCriteria, exclusionCriteria;
    List<String> questions, objectives;
    ArrayAdapter inclusionAdapter, exclusionAdapter, questionsAdapter, objectivesAdapter;
    List selectedList;
    ArrayAdapter selectedAdapter;
    private MenuItem updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_sr);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sr = (SystematicReview) extras.getSerializable("systematicReview");
        }
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("Update Systematic Review");
        binding.updateSr.TitleSR.setText(sr.getTitle());

        questions = sr.getResearchQuestions();
        questionsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, questions);
        binding.updateSr.QuestionList.setAdapter(questionsAdapter);
        registerForContextMenu(binding.updateSr.QuestionList);

        binding.updateSr.BttQuestion.setOnClickListener(questionListener);

        objectives = sr.getObjectives();
        objectivesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, objectives);
        binding.updateSr.ObjectiveList.setAdapter(objectivesAdapter);
        registerForContextMenu(binding.updateSr.ObjectiveList);

        binding.updateSr.BttObjective.setOnClickListener(objectiveListener);

        inclusionCriteria = new ArrayList<>();
        exclusionCriteria = new ArrayList<>();
        for(Criteria criteria: sr.getCriteria()){
            if(criteria.getType().equals(CriteriaType.INCLUSION)){
                inclusionCriteria.add(criteria);
            }else{
                exclusionCriteria.add(criteria);
            }
        }
        inclusionAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, inclusionCriteria);
        binding.updateSr.listViewInclusion.setAdapter(inclusionAdapter);
        registerForContextMenu(binding.updateSr.listViewInclusion);

        binding.updateSr.BttInclusionCriteria.setOnClickListener(inclusionListener);

        exclusionAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, exclusionCriteria);
        binding.updateSr.listViewExclusion.setAdapter(exclusionAdapter);
        registerForContextMenu(binding.updateSr.listViewExclusion);

        binding.updateSr.BttExclusionCriteria.setOnClickListener(exclusionListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.QuestionList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(questions.get(info.position).toString());
            String[] menuItems = getResources().getStringArray(R.array.update_sr_context_menu);
            selectedList = questions;
            selectedAdapter = questionsAdapter;
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        if (v.getId()==R.id.ObjectiveList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(objectives.get(info.position).toString());
            String[] menuItems = getResources().getStringArray(R.array.update_sr_context_menu);
            selectedList = objectives;
            selectedAdapter = objectivesAdapter;
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        if (v.getId()==R.id.listViewInclusion) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(inclusionCriteria.get(info.position).toString());
            String[] menuItems = getResources().getStringArray(R.array.update_sr_context_menu);
            selectedList = inclusionCriteria;
            selectedAdapter = inclusionAdapter;
            Criteria criteria = inclusionCriteria.get(info.position);
            for(Study s: sr.getBib().getStudies()) {
                for(ReviewedStudy rs: s.getReviewedStudies()){
                    for(int x = 0; x<rs.getReviewedCriteria().size(); x++){
                        ReviewedStudyCriteria reviewedCriteria = rs.getReviewedCriteria().get(x);
                        if(reviewedCriteria.getCriteria().equals(criteria)){
                            rs.getReviewedCriteria().remove(reviewedCriteria);
                        }
                    }
                }
            }
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        if (v.getId()==R.id.listViewExclusion) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(exclusionCriteria.get(info.position).toString());
            String[] menuItems = getResources().getStringArray(R.array.update_sr_context_menu);
            selectedList = exclusionCriteria;
            selectedAdapter = exclusionAdapter;
            Criteria criteria = exclusionCriteria.get(info.position);
            for(Study s: sr.getBib().getStudies()) {
                for(ReviewedStudy rs: s.getReviewedStudies()){
                    for(int x = 0; x<rs.getReviewedCriteria().size(); x++){
                        ReviewedStudyCriteria reviewedCriteria = rs.getReviewedCriteria().get(x);
                        if(reviewedCriteria.getCriteria().equals(criteria)){
                            rs.getReviewedCriteria().remove(reviewedCriteria);
                        }
                    }
                }
            }
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    private View.OnClickListener questionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String question = binding.updateSr.QuestionsSR.getText().toString();
            if(question.equals("")){
                Toast.makeText(UpdateSRActivity.this, "Research question can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (questions.contains(question)) {
                Toast.makeText(UpdateSRActivity.this, "Research question has already been added", Toast.LENGTH_SHORT).show();

            } else {
                questions.add(question);
                binding.updateSr.QuestionsSR.setText("");
                Toast.makeText(UpdateSRActivity.this, "Research question added", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private View.OnClickListener objectiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String objective = binding.updateSr.ObjectiveSR.getText().toString();
            if(objective.equals("")){
                Toast.makeText(UpdateSRActivity.this, "Objective can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (objectives.contains(objective)) {
                Toast.makeText(UpdateSRActivity.this, "Objective has already been added", Toast.LENGTH_SHORT).show();

            } else {
                objectives.add(objective);
                binding.updateSr.ObjectiveSR.setText("");
                Toast.makeText(UpdateSRActivity.this, "Objective added", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private View.OnClickListener exclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = binding.updateSr.CriteriaSR.getText().toString();
            if(criteria.equals("")){
                Toast.makeText(UpdateSRActivity.this, "Criteria can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.updateSr.CriteriaSR.setText("");
            if (exclusionCriteria.contains(criteria)) {
                Toast.makeText(UpdateSRActivity.this, "Criteria has already been created", Toast.LENGTH_SHORT).show();

            } else {
                Criteria c = new Criteria(criteria, CriteriaType.EXCLUSION);
                exclusionCriteria.add(c);
                for(Study s: sr.getBib().getStudies()) {
                    for(ReviewedStudy rs: s.getReviewedStudies()){
                        ReviewedStudyCriteria reviewedStudyCriteria = new ReviewedStudyCriteria();
                        reviewedStudyCriteria.setCriteria(c);
                        reviewedStudyCriteria.setSatisfied(false);
                        rs.getReviewedCriteria().add(reviewedStudyCriteria);
                    }
                }
                exclusionAdapter.notifyDataSetChanged();
                Toast.makeText(UpdateSRActivity.this, "Criteria created", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private View.OnClickListener inclusionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String criteria = binding.updateSr.CriteriaSR.getText().toString();
            if(criteria.equals("")){
                Toast.makeText(UpdateSRActivity.this, "Criteria can't be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.updateSr.CriteriaSR.setText("");
            if (inclusionCriteria.contains(criteria)) {
                Toast.makeText(UpdateSRActivity.this, "Criteria has already been created", Toast.LENGTH_SHORT).show();

            } else {
                Criteria c = new Criteria(criteria, CriteriaType.INCLUSION);
                inclusionCriteria.add(c);
                for(Study s: sr.getBib().getStudies()) {
                    for(ReviewedStudy rs: s.getReviewedStudies()){
                        ReviewedStudyCriteria reviewedStudyCriteria = new ReviewedStudyCriteria();
                        reviewedStudyCriteria.setCriteria(c);
                        reviewedStudyCriteria.setSatisfied(false);
                        rs.getReviewedCriteria().add(reviewedStudyCriteria);
                    }
                }
                inclusionAdapter.notifyDataSetChanged();
                Toast.makeText(UpdateSRActivity.this, "Criteria created", Toast.LENGTH_SHORT).show();

            }

        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.update_sr_context_menu);
        String menuItemName = menuItems[menuItemIndex];
        switch (menuItemName) {
            case "Delete":
                selectedList.remove(info.position);
                selectedAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    public void updateSR() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BACKEND_HOST)
                .build();
        MobRevSysBackendService service = retrofit.create(MobRevSysBackendService.class);
        sr.setTitle(binding.updateSr.TitleSR.getText().toString());
        List<Criteria> criterias = new ArrayList<>();
        criterias.addAll(inclusionCriteria);
        criterias.addAll(exclusionCriteria);
        sr.setCriteria(criterias);
        sr.setObjectives(objectives);
        sr.setResearchQuestions(questions);
        SharedPreferences pref = getSharedPreferences("mobrevsys", MODE_PRIVATE);
        Call<ResponseBody> response = service.updateSystematicReview(sr, pref.getString("jwt", ""));
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()){
                    case HttpURLConnection.HTTP_OK:
                        Toast.makeText(UpdateSRActivity.this, "Updated systematic review", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        Toast.makeText(UpdateSRActivity.this, "Update failed, internal server error", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update_sr_menu, menu);
        updateButton = menu.findItem(R.id.action_update);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            updateSR();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
