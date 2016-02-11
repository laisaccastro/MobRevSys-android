package com.example.laisa.entidades;

import com.example.laisa.Type.RoleType;

import java.util.List;
import java.util.Objects;

public class ReviewerRole {

    public long Id;
    private SystematicReview systematicReview;
    private Reviewer reviewer;
    private List<RoleType> roles;

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public SystematicReview getSystematicReview() {
        return systematicReview;
    }

    public void setSystematicReview(SystematicReview systematicReview) {
        this.systematicReview = systematicReview;
    }

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public List<RoleType> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleType> roles) {
        this.roles = roles;
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
        final ReviewerRole other = (ReviewerRole) obj;
        if (this.Id != other.Id) {
            return false;
        }
        if (!Objects.equals(this.systematicReview, other.systematicReview)) {
            return false;
        }
        if (!Objects.equals(this.reviewer, other.reviewer)) {
            return false;
        }
        if (!Objects.equals(this.roles, other.roles)) {
            return false;
        }
        return true;
    }
    
}
