package com.stoyanov.developer.apptracker.models;

import java.util.Date;

public class ApplicationUsed {

    private int id;
    private String appName;
    private int spendTime;
    private Date date;

    public ApplicationUsed(ApplicationUsed applicationUsed) {
        id = applicationUsed.getId();
        appName = applicationUsed.getAppName();
        spendTime = applicationUsed.getSpendTime();
        date = applicationUsed.getDate();
    }

    public ApplicationUsed() {}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(int spendTime) {
        this.spendTime = spendTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
