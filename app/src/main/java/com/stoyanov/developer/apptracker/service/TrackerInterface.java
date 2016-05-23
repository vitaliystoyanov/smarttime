package com.stoyanov.developer.apptracker.service;

public interface TrackerInterface {

    String getIDRunningApp();

    long save(String processName);
}
