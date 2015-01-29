package com.trackfox.android.services;

/**
 * Created by Sam on 15.1.2015.
 * D
 */
public interface IDeviceService {

    public void initDevice();
    public void startDeviceDiscovery();


    public void registerReceivers();
    public void unregisterReceivers();


    public void initCaches();

}
