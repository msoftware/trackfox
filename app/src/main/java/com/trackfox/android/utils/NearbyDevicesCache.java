package com.trackfox.android.utils;

import android.content.Context;

/**
 * Created by Sam on 26.1.2015..
 */
public class NearbyDevicesCache extends Cache {

    private final String TAG = PairedCache.class.getSimpleName();
    public String KEY = "NearbyDevicesCache";

    public NearbyDevicesCache(Context context) {
        super(context, "NearbyDevicesCache", ConnectedCache.class.getSimpleName());
    }
}
