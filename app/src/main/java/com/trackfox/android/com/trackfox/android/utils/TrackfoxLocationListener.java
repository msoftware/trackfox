package com.trackfox.android.com.trackfox.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Sam on 4.12.2014..
 */
public class TrackfoxLocationListener implements android.location.LocationListener {

    private final String TAG = "LocationListener";

    private SharedPreferences prefs;
    private SharedPreferences.Editor dbm;
    public String KEY_LONGITUDE = "gps_longitude";
    public String KEY_LATITUDE = "gps_latitude";
    private Activity activity;
    private LocationManager locationManager;

    public TrackfoxLocationListener(Activity activity) {
        this.activity = activity;

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    @Override
    public void onLocationChanged(Location loc) {

        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        this.dbm = prefs.edit();
        this.dbm.putString(KEY_LONGITUDE, "" + loc.getLongitude());
        this.dbm.putString(KEY_LATITUDE, "" + loc.getLatitude());
        this.dbm.commit();



        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
        /*String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        editLocation.setText(s);*/
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

}
