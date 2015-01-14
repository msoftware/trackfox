package com.trackfox.android.activities;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trackfox.android.activities.adapters.TabsPagerAdapter;
import com.trackfox.android.services.BluetoothService;
import com.trackfox.android.services.WebService;

import java.util.Set;



public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";

    private Button listBtn;
    private Button findBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private boolean BluetoothActivated;

    private Menu menu;
    private boolean waitingForBonding;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String[] tabs = { "Nearby Devices", "Trusted Devices", "Statistics" };

    protected void setBLEmenuBtn(boolean activated) {

        if (activated) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_device_bluetooth_disabled));
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_device_bluetooth));
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());


        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab_name : tabs)
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });



        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.BluetoothActivated = myBluetoothAdapter.isEnabled();


        if(myBluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                   Toast.LENGTH_LONG).show();
        } else {

            this.startServices();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        this.setBLEmenuBtn(this.BluetoothActivated);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_search_devices) {
            return true;
        }

        if (id == R.id.action_BLE_switch) {

            if (this.BluetoothActivated) {
                myBluetoothAdapter.disable();
                this.BluetoothActivated = false;
                setBLEmenuBtn(this.BluetoothActivated);
                Toast.makeText(getApplicationContext(), "Bluetooth disabled.",
                        Toast.LENGTH_LONG).show();
            } else {
                myBluetoothAdapter.enable();
                this.BluetoothActivated = true;
                setBLEmenuBtn(this.BluetoothActivated);
                Toast.makeText(getApplicationContext(), "Bluetooth enabled.",
                        Toast.LENGTH_LONG).show();
            }
        }

        if (id == R.id.action_bluetooth_service) {
            if (!isMyServiceRunning(BluetoothService.class)) {
                startService(new Intent(this, BluetoothService.class));

                Toast.makeText(
                        getApplicationContext(),
                        "Bluetooth service started...",
                        Toast.LENGTH_LONG).show();
            }
            else {
                stopService(new Intent(this, BluetoothService.class));
                stopService(new Intent(this, WebService.class));
                Toast.makeText(
                        getApplicationContext(),
                        "Bluetooth service stoped...",
                        Toast.LENGTH_LONG).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }


    private void startServices() {
        if (!isMyServiceRunning(BluetoothService.class)) {
            startService(new Intent(this, BluetoothService.class));
        }
        if (!isMyServiceRunning(WebService.class)) {
            startService(new Intent(this, WebService.class));
        }
        Toast.makeText(
                getApplicationContext(),
                "Services started...",
                Toast.LENGTH_LONG).show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
