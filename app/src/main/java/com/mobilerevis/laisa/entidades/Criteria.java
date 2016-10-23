package com.mobilerevis.laisa.entidades;

import com.mobilerevis.laisa.Type.CriteriaType;

import java.io.Serializable;

public class Criteria implements Serializable{

    private long id;
    private String description;
    private CriteriaType type;

    public Criteria(String description, CriteriaType type) {
        this.description = description;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CriteriaType getType() {
        return type;
    }

    public void setType(CriteriaType type) {
        this.type = type;
    }

    @Override
    public String toString(){
       return description;
    }
}
