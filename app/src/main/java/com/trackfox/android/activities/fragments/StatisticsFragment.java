package com.trackfox.android.activities.fragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trackfox.android.activities.R;
import com.trackfox.android.utils.IMEICache;
import com.trackfox.android.utils.PairedCache;
import com.trackfox.android.utils.TrackfoxLocationListener;

/**
 * Created by Sam on 1.12.2014..
 */
public class StatisticsFragment extends Fragment {

    private String DEBUG = "StatisticsFragment";

    private TrackfoxLocationListener tLocationManager;
    private SharedPreferences prefs;

    private TextView locationTextView;
    private TextView deviceIDTextView;
    private TextView numberOfDevices;

    private String longitude;
    private String latitude;

    private PairedCache pairedCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        Log.d(DEBUG, "View created");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationTextView = (TextView) getActivity().findViewById(R.id.location_text_view);
        deviceIDTextView = (TextView) getActivity().findViewById(R.id.device_id_text_view);
        numberOfDevices = (TextView) getActivity().findViewById(R.id.number_of_devices_text_view);

        tLocationManager = new TrackfoxLocationListener(getActivity());

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                        Log.d("prefs_changed", s);
                    }
                }
        );


        longitude = prefs.getString("gps_longitude", null);
        latitude = prefs.getString("gps_latitude", null);
        if (longitude == null || latitude == null) {
            Location loc = tLocationManager.getLastBestLocation();
            longitude = "" + loc.getLongitude();
            latitude = "" + loc.getLatitude();
        }

        String locationText = "Location: " + longitude + ", " + latitude;
        Log.d(DEBUG, locationText);
        locationTextView.setText(locationText);

        IMEICache imei = new IMEICache(getActivity());
        String deviceid = imei.read();
        String deviceid_text = "DeviceID: " + deviceid;
        deviceIDTextView.setText(deviceid_text);


        int devicesSize = pairedCache.getList().size();
        numberOfDevices.setText("Trusted Devices: " + devicesSize);

    }
}
