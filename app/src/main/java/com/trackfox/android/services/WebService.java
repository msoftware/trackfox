package com.trackfox.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.trackfox.android.utils.PairedCache;

/**
 * Created by Sam on 20.12.2014.
 */
public class WebService extends Service {

    private static final String TAG = WebService.class.getSimpleName();
    PairedCache pairedCache;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pairedCache = new PairedCache(this);


        Log.d(TAG, "onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStarted");

        if (pairedCache.getList().size() == 0) {
            // TODO: send data to server
            Log.d(TAG, "Sending data to server");
        }

        return super.onStartCommand(intent, flags, startId);
    }




}
