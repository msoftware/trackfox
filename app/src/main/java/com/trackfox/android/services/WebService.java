package com.trackfox.android.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.trackfox.android.utils.ConnectedCache;
import com.trackfox.android.utils.IMEICache;
import com.trackfox.android.utils.PairedCache;

import org.apache.http.Header;

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
    private static final String endpoint = "http://arka.foi.hr/~hrorejas/TrackFox/scripts/api.php";

    PairedCache pairedCache;
    ConnectedCache connectedCache;
    IMEICache imeiCache;

    private SharedPreferences prefs;
    private String deviceID;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pairedCache = new PairedCache(this);
        connectedCache = new ConnectedCache(this);
        imeiCache = new IMEICache(this);
        this.deviceID = imeiCache.read();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

    /*
    public void httpPostData() {
        // Creating a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(endpoint);

        try {
            // Adding post data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("deviceid", this.deviceID));
            nameValuePairs.add(new BasicNameValuePair("sessionid", this.deviceID));
            nameValuePairs.add(new BasicNameValuePair("long", prefs.getString("gps_longitude", null)));
            nameValuePairs.add(new BasicNameValuePair("lat", prefs.getString("gps_longitude", null)));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            Log.d(TAG, "Sending http post data");
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);



        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
    */


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

                        if (size == 0) {
                            Log.d(TAG, "We are not connected with trusted device. Sending data to server.");

                            /*
                            HashMap<String, String> data = new HashMap<String, String>();
                            data.put("deviceid", deviceID);
                            data.put("long", prefs.getString("gps_longitude", null));
                            data.put("lat", prefs.getString("gps_latitude", null));
                            data.put("sessionid", deviceID);
                            AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
                            asyncHttpPost.execute(endpoint);
                            */

                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("deviceid", deviceID);
                            params.put("sessionid", deviceID);
                            params.put("lat", prefs.getString("gps_latitude", null));
                            params.put("long", prefs.getString("gps_longitude", null));
                            client.post(endpoint, params , new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    Log.d(TAG, "HURAY");
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                    Log.d(TAG, ":( -> " + i);
                                }
                            });
                        } else {
                            Log.d(TAG, "We are connected with trusted device.");
                        }
                    }
                });
            }
        };
    }



}
