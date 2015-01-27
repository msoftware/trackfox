package com.trackfox.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.trackfox.android.utils.ConnectedCache;
import com.trackfox.android.utils.PairedCache;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sam on 20.12.2014.
 */
public class WebService extends Service {

    Timer timer;
    TimerTask timerTask;

    final Handler handler = new Handler();

    private static final String TAG = WebService.class.getSimpleName();

    PairedCache pairedCache;
    ConnectedCache connectedCache;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pairedCache = new PairedCache(this);
        connectedCache = new ConnectedCache(this);

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
        startTimer();


        // TODO: add alarm to periodacly check cache for paired devices
        //if (pairedCache.getList().size() == 0) {
        //         INFO: send data to server
        //    Log.d(TAG, "Sending data to server");
        //}

        return super.onStartCommand(intent, flags, startId);
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());
                        int size  = connectedCache.getList().size();
                        //show the toast
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), strDate + ": " + size, duration);
                        toast.show();
                    }
                });
            }
        };
    }



}
