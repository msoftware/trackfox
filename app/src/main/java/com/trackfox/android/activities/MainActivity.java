package com.trackfox.android.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trackfox.android.activities.adapters.TabsPagerAdapter;

import java.lang.reflect.Method;
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


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                //text.setText("Status: Enabled");
            } else {
                //text.setText("Status: Disabled");
            }
        }
    }




    public void list(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                unpairDevice(device);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find(View view) {

        if (myBluetoothAdapter.isDiscovering()) {
            Toast.makeText(getApplicationContext(),"Canceling search, please wait... " ,
                    Toast.LENGTH_LONG).show();
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            Toast.makeText(getApplicationContext(),"Searching, please wait..." ,
                    Toast.LENGTH_LONG).show();

            myBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }




    private void pairDevice(BluetoothDevice device) {
        try {
            waitingForBonding = true;

            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

            Log.d(TAG, "REFLECTION FOR createBond invoked.");
            // TODO: disable pairing button
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
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


        return super.onOptionsItemSelected(item);
    }

}
