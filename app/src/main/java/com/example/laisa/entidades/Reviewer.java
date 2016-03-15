package com.example.laisa.entidades;

import java.util.List;
import java.util.Objects;

public class Reviewer {

    private String name;
    private String email;
    private String password;
    private String affiliatedUniversity;
    private String country;
    private List<SystematicReview> reviews;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAffiliatedUniversity() {
        return affiliatedUniversity;
    }

    public void setAffiliatedUniversity(String affiliatedUniversity) {
        this.affiliatedUniversity = affiliatedUniversity;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<SystematicReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<SystematicReview> reviews) {
        this.reviews = reviews;
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
        final Reviewer other = (Reviewer) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        if (!Objects.equals(this.reviews, other.reviews)) {
            return false;
        }
        return true;
    }
    
    
}
