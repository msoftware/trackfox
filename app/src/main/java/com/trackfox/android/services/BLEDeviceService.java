package com.trackfox.android.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.trackfox.android.models.BLEDeviceModel;
import com.trackfox.android.utils.ConnectedCache;
import com.trackfox.android.utils.NearbyDevicesCache;
import com.trackfox.android.utils.PairedCache;

import java.util.Set;


/**
 * Created by Sam on 20.12.2014.
 */

public class BLEDeviceService extends Service implements IDeviceService {

    private static final String TAG = BLEDeviceService.class.getSimpleName();
    private BluetoothAdapter myBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    private PairedCache pairedCache;
    private NearbyDevicesCache nearbyDevicesCache;
    private ConnectedCache connectedCache;

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Device found
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BLEDeviceModel deviceModel = new BLEDeviceModel(device.getName(), device.getAddress(), device.getBondState());
                Log.d(TAG,
                        deviceModel.getTitle() + ": "
                                + deviceModel.getMacAddress() + "\n "
                                + deviceModel.getBondState() + ": "
                                + deviceModel.getBondStateCode());

                deviceModel.setDevice(device);

                if (device.getBondState() == 12) {
                    // TODO: add temporal signature to deviceModel
                    connectedCache.add(deviceModel);
                    connectedCache.commitList();
                }

                nearbyDevicesCache.add(deviceModel);
                nearbyDevicesCache.commitList();

                Log.d(TAG, "ACTION_FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                // Device connected

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                BLEDeviceModel deviceModel = new BLEDeviceModel(device);
                deviceModel.setDevice(device);
                connectedCache.add(deviceModel);
                connectedCache.commitList();

                nearbyDevicesCache.add(deviceModel);
                nearbyDevicesCache.commitList();
                Log.d(TAG, "ACTION_ACL_CONNECTED");

            }

            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                // Device has disconnected
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "ACL_DISCONNECTED");
                connectedCache.remove(new BLEDeviceModel(device));
                connectedCache.commitList();

                nearbyDevicesCache.remove(new BLEDeviceModel(device));
                nearbyDevicesCache.commitList();
            }


            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Done searching
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                // Device is about to disconnect
                Log.d(TAG, "ACTION_ACL_DISCONNECT_REQUESTED");
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
        this.initCaches();
        this.initDevice();
        this.registerReceivers();
        this.startDeviceDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyed");
        this.unregisterReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        this.startDeviceDiscovery();

        return super.onStartCommand(intent, flags, startId);
    }


    // INFO: following is implementation of interface

    public void initDevice() {
        myBluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        pairedDevices = myBluetoothAdapter.getBondedDevices();
    }

    public void startDeviceDiscovery() {
        myBluetoothAdapter.startDiscovery();
    }

    public void registerReceivers() {
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    public void unregisterReceivers() {
        this.unregisterReceiver(bReceiver);
    }

    public void initCaches() {
        pairedCache = new PairedCache(this);
        connectedCache = new ConnectedCache(this);

        nearbyDevicesCache = new NearbyDevicesCache(this);
    }

}
