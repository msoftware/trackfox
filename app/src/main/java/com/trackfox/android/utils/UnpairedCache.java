package com.trackfox.android.utils;

import android.content.Context;

/**
 * Created by Sam on 10.1.2015..
 */
public class UnpairedCache extends Cache {

    private final String TAG = UnpairedCache.class.getSimpleName();
    private String KEY = "UnpairedCache";


    public UnpairedCache(Context context) {
        super(context, "UnpairedCache", UnpairedCache.class.getSimpleName());
    }


}
