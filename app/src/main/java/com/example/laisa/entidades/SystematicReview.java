package com.example.laisa.entidades;

import com.example.laisa.Type.PaperDivisionType;
import com.example.laisa.Type.RoleType;
import com.example.laisa.Type.StageType;
import com.example.laisa.Util;
import com.example.laisa.tcc.InicialActivity;
import com.example.laisa.tcc.ListSRActivity;
import com.example.laisa.tcc.MyApplication;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SystematicReview implements Serializable{

    private long id;
    private Reviewer owner;
    private String title;
    private List<String> objectives;
    private List<String> researchQuestions;
    private List<Criteria> criteria;
    private List<ReviewerRole> participatingReviewers;
    private BibFile bib;
    private PaperDivisionType divisionType;
    private StageType stage;


    public long getId() {
        return id;
    }

    public void setId(long Id) {
        this.id = Id;
    }

    public Reviewer getOwner() {
        return owner;
    }

    public void setOwner(Reviewer owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getResearchQuestions() {
        return researchQuestions;
    }

    public void setResearchQuestions(List<String> researchQuestions) {
        this.researchQuestions = researchQuestions;
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }

    public List<ReviewerRole> getParticipatingReviewers() {
        return participatingReviewers;
    }

    public void setParticipatingReviewers(List<ReviewerRole> participatingReviewers) {
        this.participatingReviewers = participatingReviewers;
    }

    public BibFile getBib() {
        return bib;
    }

    public void setBib(BibFile bib) {
        this.bib = bib;
    }

    public PaperDivisionType getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(PaperDivisionType divisionType) {
        this.divisionType = divisionType;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }

    public StageType getStage() {
        return stage;
    }

    public void setStage(StageType stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        String useremail = MyApplication.getCurrentUserEmail();
        String role = (useremail.equals(owner.getEmail())) ? "Owner" : "Participant";
        String participantRole = "";
        if(role.equals("Participant")){
            for(ReviewerRole rr: participatingReviewers){
                if(rr.getReviewer().getEmail().equals(useremail)){
                    participantRole = "\n";
                    for(RoleType roletype: rr.getRoles()){
                        participantRole = participantRole + roletype.name()+"\t";
                    }
                }
            }
        }
        return  "Title: " + title +
                "\n"+role+
                participantRole+
                "\n"+"Current Stage: "+stage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SystematicReview other = (SystematicReview) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        if (!Objects.equals(this.researchQuestions, other.researchQuestions)) {
            return false;
        }
        if (!Objects.equals(this.criteria, other.criteria)) {
            return false;
        }
        if (!Objects.equals(this.participatingReviewers, other.participatingReviewers)) {
            return false;
        }
        if (!Objects.equals(this.bib, other.bib)) {
            return false;
        }
        if (!Objects.equals(this.divisionType, other.divisionType)){
            return false;
        }
        return true;
    }
    
}
