package com.example.job_code.dto;

import com.example.job_code.model.JobType;

import java.math.BigDecimal;

public class JobSearchCriteria {

    private String keyword;
    private String location;
    private JobType jobType;
    private BigDecimal minSalary;

    public String getKeyword() {
        return keyword;
    }

    public String getLocation() {
        return location;
    }

    public JobType getJobType() {
        return jobType;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }
}
