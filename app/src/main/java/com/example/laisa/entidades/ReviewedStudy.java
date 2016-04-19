package com.example.laisa.entidades;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.laisa.Type.IncludeType;

import java.io.Serializable;
import java.util.List;

public class ReviewedStudy implements Serializable{

    private long id;
    private transient Study study;
    private Reviewer reviewer;

    private List<ReviewedStudyCriteria> reviewedCriteria;

    private IncludeType includedInitialSelection = null;

    private IncludeType includedFinalSelection = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public List<ReviewedStudyCriteria> getReviewedCriteria() {
        return reviewedCriteria;
    }

    public void setReviewedCriteria(List<ReviewedStudyCriteria> reviewedCriteria) {
        this.reviewedCriteria = reviewedCriteria;
    }

    public IncludeType getIncludedInitialSelection() {
        return includedInitialSelection;
    }

    public void setIncludedInitialSelection(IncludeType includedInitialSelection) {
        this.includedInitialSelection = includedInitialSelection;
    }

    public IncludeType getIncludedFinalSelection() {
        return includedFinalSelection;
    }

    public void setIncludedFinalSelection(IncludeType includedFinalSelection) {
        this.includedFinalSelection = includedFinalSelection;
    }
}
