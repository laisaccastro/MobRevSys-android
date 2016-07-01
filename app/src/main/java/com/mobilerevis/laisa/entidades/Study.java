package com.mobilerevis.laisa.entidades;

import com.mobilerevis.laisa.Type.IncludeType;

import java.io.Serializable;
import java.util.List;

public class Study implements Serializable{

    private long id;
    private String title;
    private String studyAbstract;
    private List<ReviewedStudy> reviewedStudies;

    private IncludeType includedInitialReview = IncludeType.EXCLUDED;
    private IncludeType includedFinalReview = IncludeType.EXCLUDED;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudyAbstract() {
        return studyAbstract;
    }

    public void setStudyAbstract(String studyAbstract) {
        this.studyAbstract = studyAbstract;
    }

    public List<ReviewedStudy> getReviewedStudies() {
        return reviewedStudies;
    }

    public void setReviewedStudies(List<ReviewedStudy> reviewedStudies) {
        this.reviewedStudies = reviewedStudies;
    }

    public IncludeType getIncludedInitialReview() {
        return includedInitialReview;
    }

    public void setIncludedInitialReview(IncludeType includedInitialReview) {
        this.includedInitialReview = includedInitialReview;
    }

    public IncludeType getIncludedFinalReview() {
        return includedFinalReview;
    }

    public void setIncludedFinalReview(IncludeType includedFinalReview) {
        this.includedFinalReview = includedFinalReview;
    }

    @Override
    public String toString() {
        return title;
    }
}
