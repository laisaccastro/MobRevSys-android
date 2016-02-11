package com.example.laisa.entidades;

import java.util.List;

public class ReviewedStudy {

    private long Id;
    private Study study;
    private Reviewer reviewer;
    private boolean included;
    private List<ReviewedStudyCriteria> reviewedCriteria;
}
