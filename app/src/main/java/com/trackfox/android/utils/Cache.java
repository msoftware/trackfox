package com.trackfox.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trackfox.android.models.BLEDeviceModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Sam on 13.1.2015..
 */
public class Cache {

    Set<BLEDeviceModel> dbList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor dbm;
    private String KEY, TAG;
    private GsonBuilder gsonb;
    private Gson gson;

    public Cache(Context context, String KEY, String TAG) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.dbm = prefs.edit();
        gsonb = new GsonBuilder();
        gson = gsonb.create();
        this.TAG = TAG;
        this.KEY = KEY;
        dbList = this.getList();
    }

    public Set<BLEDeviceModel> getList() {
        Set<BLEDeviceModel> tmp;

        if (prefs.contains(KEY)) {
            String value = prefs.getString(KEY, null);
            BLEDeviceModel[] devices = gson.fromJson(value, BLEDeviceModel[].class);
            tmp = new HashSet<BLEDeviceModel>(Arrays.asList(devices));
        } else {

            tmp = new HashSet<BLEDeviceModel>();
            dbm.putString(KEY, "[]");
            dbm.commit();
        }

        return tmp;
    }

    public void commitList() {
        Log.d(TAG, "Saving to SharedPreferences");
        String value = gson.toJson(dbList);
        dbm.putString(KEY, value);
        dbm.commit();
    }

    public void add(BLEDeviceModel item) {
        boolean itemExists = this.exists(item);

        Log.d(TAG, "itemExists: " + itemExists);
        if (!itemExists) {
            this.dbList.add(item);
        }
    }

    public boolean remove(BLEDeviceModel item) {
        boolean returnValue = false;
        Iterator it = this.dbList.iterator();
        while (it.hasNext()) {
            BLEDeviceModel model = (BLEDeviceModel) it.next();
            if (model.getMacAddress().equals(item.getMacAddress())) {
                it.remove();
                returnValue = true;
            }
        }
        return returnValue;
    }

    public boolean exists(BLEDeviceModel item) {
        for (BLEDeviceModel model : this.dbList) {

            Log.d(TAG, "Checking: " + model.getMacAddress() + " == "
                    + item.getMacAddress());

            if (model.getMacAddress().equals(item.getMacAddress())) {
                Log.d(TAG, " [+] model == item (" + model.getMacAddress() + " == " + item.getMacAddress());
                return true;
            }

        }
        return false;
    }
}
