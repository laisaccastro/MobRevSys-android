package com.example.laisa.tcc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laisa.Type.CriteriaType;
import com.example.laisa.Type.IncludeType;
import com.example.laisa.Type.StageType;
import com.example.laisa.Util;
import com.example.laisa.adapters.ReviewedStudyCriteriaAdapter;
import com.example.laisa.entidades.Criteria;
import com.example.laisa.entidades.ReviewedStudy;
import com.example.laisa.entidades.ReviewedStudyCriteria;
import com.example.laisa.entidades.Reviewer;
import com.example.laisa.entidades.Study;

import java.util.ArrayList;
import java.util.List;


public class ReadSRFragment extends Fragment {
    private static final String ARG_STUDY = "study";
    private static final String ARG_CRITERIA = "criteria";
    private static final String ARG_STAGE = "stage";

    private Study mStudy;
    private ReviewedStudy mReviewedStudy;
    private List<Criteria> mCriteria;
    private StageType mStage;
    private ArrayList<ReviewedStudyCriteria> mReviewedInclusionCriteria;
    private ArrayList<ReviewedStudyCriteria> mReviewedExclusionCriteria;

    private OnFragmentInteractionListener mListener;

    private TextView textViewTitle, textViewAbstract;
    private Button includeBt, excludeBt, doubtBt, commentBt;

    private ReviewedStudyCriteriaAdapter inclusionAdapter, exclusionAdapter;
    private ListView inclusionListView, exclusionListView;

    public ReadSRFragment() {
        // Required empty public constructor
    }

    public static ReadSRFragment newInstance(Study study, ArrayList<Criteria> criteria, StageType stage) {
        ReadSRFragment fragment = new ReadSRFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STUDY, study);
        args.putSerializable(ARG_CRITERIA, criteria);
        args.putSerializable(ARG_STAGE, stage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudy = (Study) getArguments().getSerializable(ARG_STUDY);
            mStage = (StageType) getArguments().getSerializable(ARG_STAGE);
            for (ReviewedStudy reviewedStudy : mStudy.getReviewedStudies()) {
                if (reviewedStudy.getReviewer().getEmail().equals(MyApplication.getCurrentUserEmail())) {
                    mReviewedStudy = reviewedStudy;
                }
            }
            if (mReviewedStudy == null) {
                mReviewedStudy = new ReviewedStudy();
                Reviewer r = new Reviewer();
                r.setEmail(MyApplication.getCurrentUserEmail());
                mReviewedStudy.setReviewer(r);
                mReviewedStudy.setStudy(mStudy);
                if (mStudy.getReviewedStudies() != null) {
                    mStudy.getReviewedStudies().add(mReviewedStudy);
                } else {
                    mStudy.setReviewedStudies(new ArrayList<ReviewedStudy>());
                    mStudy.getReviewedStudies().add(mReviewedStudy);
                }

            }
            mCriteria = (List<Criteria>) getArguments().getSerializable(ARG_CRITERIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_sr, container, false);
        textViewTitle = (TextView) view.findViewById(R.id.TextTitleSR);
        textViewTitle.setText(mStudy.getTitle());
        textViewAbstract = (TextView) view.findViewById(R.id.TextAbstractSR);
        textViewAbstract.setText(mStudy.getStudyAbstract());
        inclusionListView = (ListView) view.findViewById(R.id.listViewInclusion);
        exclusionListView = (ListView) view.findViewById(R.id.listViewExclusion);
        includeBt = (Button) view.findViewById(R.id.BttInclusion);
        includeBt.setOnClickListener(inclusionClickListener);
        excludeBt = (Button) view.findViewById(R.id.BttExclusion);
        excludeBt.setOnClickListener(exclusionClickListener);
        doubtBt = (Button) view.findViewById(R.id.BttUndecided);
        doubtBt.setOnClickListener(doubtClickListener);
        commentBt = (Button) view.findViewById(R.id.BttComment);
        commentBt.setOnClickListener(commentClickListener);

        updateListView();
        return view;
    }

    public void updateView(Study study, List<Criteria> criteria, StageType stage) {
        int index = mStudy.getReviewedStudies().indexOf(mReviewedStudy);
        List<ReviewedStudyCriteria> newReviewedCriteria = new ArrayList<>();
        newReviewedCriteria.addAll(mReviewedInclusionCriteria);
        newReviewedCriteria.addAll(mReviewedExclusionCriteria);
        mReviewedStudy.setReviewedCriteria(newReviewedCriteria);
        mStudy.getReviewedStudies().set(index, mReviewedStudy);
        mListener.onStudyInteraction(mStudy);
        mStudy = study;
        mReviewedStudy = null;
        for (ReviewedStudy reviewedStudy : mStudy.getReviewedStudies()) {
            if (reviewedStudy.getReviewer().getEmail().equals(MyApplication.getCurrentUserEmail())) {
                mReviewedStudy = reviewedStudy;
            }
        }
        if (mReviewedStudy == null) {
            mReviewedStudy = new ReviewedStudy();
            Reviewer r = new Reviewer();
            r.setEmail(MyApplication.getCurrentUserEmail());
            mReviewedStudy.setReviewer(r);
            mReviewedStudy.setStudy(mStudy);
            if (mStudy.getReviewedStudies() != null) {
                mStudy.getReviewedStudies().add(mReviewedStudy);
            } else {
                mStudy.setReviewedStudies(new ArrayList<ReviewedStudy>());
                mStudy.getReviewedStudies().add(mReviewedStudy);
            }

        }
        mCriteria = criteria;
        mStage = stage;
        textViewTitle = (TextView) getView().findViewById(R.id.TextTitleSR);
        textViewTitle.setText(mStudy.getTitle());
        textViewAbstract = (TextView) getView().findViewById(R.id.TextAbstractSR);
        textViewAbstract.setText(mStudy.getStudyAbstract());
        updateListView();
    }

    public void updateListView() {
        mReviewedInclusionCriteria = new ArrayList<>();
        mReviewedExclusionCriteria = new ArrayList<>();
        for (Criteria c : mCriteria) {
            ReviewedStudyCriteria rsc = null;
            if (mReviewedStudy.getReviewedCriteria() == null) {
                mReviewedStudy.setReviewedCriteria(new ArrayList<ReviewedStudyCriteria>());
            }
            for (ReviewedStudyCriteria existingRSC : mReviewedStudy.getReviewedCriteria()) {
                if (existingRSC.getCriteria().getDescription().equals(c.getDescription())) {
                    rsc = existingRSC;
                    break;
                }
            }
            if (rsc == null) {
                rsc = new ReviewedStudyCriteria();
                rsc.setCriteria(c);
            }
            if (rsc.getCriteria().getType().equals(CriteriaType.INCLUSION)) {
                mReviewedInclusionCriteria.add(rsc);
            } else {
                mReviewedExclusionCriteria.add(rsc);
            }
        }
        inclusionAdapter = new ReviewedStudyCriteriaAdapter(getContext(), R.layout.inclusion_list_layout, mReviewedInclusionCriteria);
        inclusionListView.setAdapter(inclusionAdapter);
        inclusionAdapter.notifyDataSetChanged();
        exclusionAdapter = new ReviewedStudyCriteriaAdapter(getContext(), R.layout.inclusion_list_layout, mReviewedExclusionCriteria);
        exclusionListView.setAdapter(exclusionAdapter);
        exclusionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onStudyInteraction(Study study);
    }

    private View.OnClickListener inclusionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean anyInclusionSatisfied = false;
            for (ReviewedStudyCriteria reviewedStudyCriteria : mReviewedInclusionCriteria) {
                if (reviewedStudyCriteria.isSatisfied()) {
                    anyInclusionSatisfied = true;
                }
            }
            if (anyInclusionSatisfied) {
                if (mStage.equals(StageType.INITIAL_SELECTION)) {
                    mReviewedStudy.setIncludedInitialSelection(IncludeType.INCLUDED);
                } else {
                    mReviewedStudy.setIncludedFinalSelection(IncludeType.INCLUDED);
                }
                mListener.onStudyInteraction(mStudy);
            } else {
                Toast.makeText(getContext(), "No inclusion criteria satisfied, can't include study!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener exclusionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean anyExclusionSatisfied = false;
            for (ReviewedStudyCriteria reviewedStudyCriteria : mReviewedExclusionCriteria) {
                if (reviewedStudyCriteria.isSatisfied()) {
                    anyExclusionSatisfied = true;
                }
            }
            if (anyExclusionSatisfied) {
                if (mStage.equals(StageType.INITIAL_SELECTION)) {
                    mReviewedStudy.setIncludedInitialSelection(IncludeType.EXCLUDED);
                } else {
                    mReviewedStudy.setIncludedFinalSelection(IncludeType.EXCLUDED);
                }
                mListener.onStudyInteraction(mStudy);
            } else {
                Toast.makeText(getContext(), "No exclusion criteria satisfied, can't exclude study!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener doubtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStage.equals(StageType.INITIAL_SELECTION)) {
                mReviewedStudy.setIncludedInitialSelection(IncludeType.DOUBT);
            } else {
                mReviewedStudy.setIncludedFinalSelection(IncludeType.DOUBT);
            }
            mListener.onStudyInteraction(mStudy);
        }
    };

    private View.OnClickListener commentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = (EditText) getView().findViewById(R.id.Comment);
            et.setText("");
            Toast.makeText(getContext(), "Added comment", Toast.LENGTH_SHORT).show();
        }
    };
}
