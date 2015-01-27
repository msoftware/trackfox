package com.trackfox.android.utils;

import android.content.Context;

/**
 * Created by Sam on 13.1.2015..
 */
public class PairedCache extends Cache {

    private final String TAG = PairedCache.class.getSimpleName();
    public String KEY = "PairedCache";

    public PairedCache(Context context) {
        super(context, "PairedCache", PairedCache.class.getSimpleName());

    }
}
