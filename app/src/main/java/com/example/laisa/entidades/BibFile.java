package com.example.laisa.entidades;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BibFile implements Serializable{

    private long id;
    private String name;
    private List<Study> studies;

    public long getId() {
        return id;
    }

    public void setId(long Id) {
        this.id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
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
        final BibFile other = (BibFile) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.studies, other.studies)) {
            return false;
        }
        return true;
    }

}
