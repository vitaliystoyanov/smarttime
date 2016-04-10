package com.stoyanov.developer.apptracker.service;

public interface TrackerIterface {

    String getRunningApplication();

    long save(String processName);
}
