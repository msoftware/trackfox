package com.trackfox.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sam on 24.1.2015..
 */

public class IMEICache {

    private SharedPreferences prefs;
    private SharedPreferences.Editor dbm;

    public IMEICache(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.dbm = prefs.edit();
    }


    public void save(String imei) {
        this.dbm.putString("imei", imei);
        this.dbm.commit();
    }

    public String read() {
        return this.prefs.getString("imei", null);
    }

}
