package com.stoyanov.developer.apptracker.models;

import java.util.Date;

public class ApplicationUsed implements Comparable<ApplicationUsed> {

    private int id;
    private String appName;
    private int spendTime;
    private Date date;

    public ApplicationUsed(ApplicationUsed applicationUsed) {
        id = applicationUsed.getId();
        appName = applicationUsed.getApplicationName();
        spendTime = applicationUsed.getSpentTime();
        date = applicationUsed.getDate();
    }

    public ApplicationUsed() {
    }

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

    public int getSpentTime() {
        return spendTime;
    }

    public void setSpendTime(int spendTime) {
        this.spendTime = spendTime;
    }

    public String getApplicationName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public int compareTo(ApplicationUsed another) {
        return another.getSpentTime() - this.spendTime;
    }

    @Override
    public String toString() {
        return "ApplicationUsed{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", spendTime=" + spendTime +
                ", date=" + date +
                '}';
    }
}
