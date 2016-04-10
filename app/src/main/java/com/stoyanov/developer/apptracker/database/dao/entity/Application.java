package com.stoyanov.developer.apptracker.database.dao.entity;

import java.util.Date;

public class Application {

    private int id;
    private String appPackage;
    private Date datetime;
    private int spendTime;

    public String getAppPackage() {
        return appPackage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }


    public int getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(int seconds) {
        this.spendTime = seconds;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", appPackage='" + appPackage + '\'' +
                ", spendTime=" + spendTime +
                '}';
    }
}
