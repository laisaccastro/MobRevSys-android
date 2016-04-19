package com.example.laisa.entidades;

import java.io.Serializable;

public class ReviewedStudyCriteria implements Serializable {

    private long id;
    private Criteria criteria;
    private boolean satisfied;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    @Override
    public String toString() {
        return criteria.getDescription();
    }
}
