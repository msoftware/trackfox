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

import com.trackfox.android.models.DeviceModel;
import com.trackfox.android.utils.PairedCache;
import com.trackfox.android.utils.UnpairedCache;


/**
 * Created by Sam on 20.12.2014.
 */

public class BluetoothService extends Service {

    private static final String TAG = BluetoothService.class.getSimpleName();
    private BluetoothAdapter myBluetoothAdapter;

    private UnpairedCache unpairedCache;
    private PairedCache pairedCache;

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Device found
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG,
                        device.getName() + ": "
                                + device.getAddress() + "\n "
                                + device.getBondState() + ": "
                                + device.getUuids());
                DeviceModel deviceModel = new DeviceModel(device.getName(), device.getAddress(), device.getBondState());
                deviceModel.setDevice(device);

                if (device.getBondState() != 12) {
                    unpairedCache.add(deviceModel);
                    unpairedCache.commitList();
                }



                Log.d(TAG, "ACTION_FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                // Device connected

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                DeviceModel deviceModel = new DeviceModel(device);
                deviceModel.setDevice(device);
                pairedCache.add(deviceModel);
                pairedCache.commitList();

                Log.d(TAG, "ACTION_ACL_CONNECTED");

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Done searching
                Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                // Device is about to disconnect
                Log.d(TAG, "ACTION_ACL_DISCONNECT_REQUESTED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                // Device has disconnected
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "ACL_DISCONNECTED");

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
        unpairedCache = new UnpairedCache(this);
        pairedCache = new PairedCache(this);
        //myBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyed");
        unregisterReceiver(bReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myBluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
        this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

        myBluetoothAdapter.startDiscovery();
        return super.onStartCommand(intent, flags, startId);
    }

}
