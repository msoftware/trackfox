package com.trackfox.android.utils;

import android.content.Context;

/**
 * Created by Sam on 19.1.2015..
 */
public class ConnectedCache extends Cache {

    private final String TAG = PairedCache.class.getSimpleName();
    public String KEY = "ConnectedCache";

    public ConnectedCache(Context context) {
        super(context, "ConnectedCache", ConnectedCache.class.getSimpleName());
    }
}
