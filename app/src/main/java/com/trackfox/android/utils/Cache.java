package com.trackfox.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trackfox.android.models.DeviceModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sam on 13.1.2015..
 */
public class Cache {

    Set<DeviceModel> dbList;
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

        this.KEY = KEY;
        dbList = this.getList();
    }

    public Set<DeviceModel> getList() {
        Set<DeviceModel> tmp;

        if (prefs.contains(KEY)) {
            String value = prefs.getString(KEY, null);
            DeviceModel[] devices = gson.fromJson(value, DeviceModel[].class);
            tmp = new HashSet<DeviceModel>(Arrays.asList(devices));
        } else {

            tmp = new HashSet<DeviceModel>();
            dbm.putString(KEY, "[]");
            dbm.commit();
        }

        return tmp;
    }

    public void saveList() {
        Log.d(TAG, "Saving to SharedPreferences");
        String value = gson.toJson(dbList);
        dbm.putString(KEY, value);
        dbm.commit();
    }

    public void add(DeviceModel item) {
        boolean itemExists = this.exists(item);

        Log.d(TAG, "itemExists: " + itemExists);
        if (!itemExists) {
            this.dbList.add(item);
        }
    }

    public boolean exists(DeviceModel item) {
        for (DeviceModel model : this.dbList) {

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
