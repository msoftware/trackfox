package com.trackfox.android.activities.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.trackfox.android.activities.fragments.NewDevicesFragment;
import com.trackfox.android.activities.fragments.StatisticsFragment;
import com.trackfox.android.activities.fragments.TrustedDevicesFragment;

/**
 * Created by Sam on 1.12.2014..
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new NewDevicesFragment();
            case 1:
                return new TrustedDevicesFragment();
            case 2:
                return new StatisticsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
