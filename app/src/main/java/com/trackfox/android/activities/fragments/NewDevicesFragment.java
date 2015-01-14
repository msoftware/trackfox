package com.trackfox.android.activities.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.trackfox.android.activities.R;
import com.trackfox.android.activities.adapters.DeviceListAdapter;
import com.trackfox.android.utils.DevicePreferences;
import com.trackfox.android.models.DeviceModel;

/**
 * Created by Sam on 1.12.2014..
 */
public class NewDevicesFragment extends Fragment {

    private String TAG = "NewDevicesFragment";
    private ListView deviceListView;
    private DeviceListAdapter BTArrayAdapter;

    public boolean waitingForBonding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private android.bluetooth.BluetoothAdapter myBluetoothAdapter;

    private DevicePreferences dPreferences;

    //Set<DeviceModel> dbList;
    //private SharedPreferences prefs;
    //private SharedPreferences.Editor dbm;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle saveInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_new_devices,
                container,
                false);

        deviceListView = (ListView) rootView.findViewById(R.id.new_devices_list);
        BTArrayAdapter = new DeviceListAdapter(
                getActivity().getApplicationContext());
                //new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(BTArrayAdapter);

        IntentFilter aclConnected = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter aclDisconnectRequest = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter aclDisconnected = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        getActivity().registerReceiver(bReceiver, aclConnected);
        getActivity().registerReceiver(bReceiver, aclDisconnectRequest);
        getActivity().registerReceiver(bReceiver, aclDisconnected);

        return rootView;

    }


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
                BTArrayAdapter.add(deviceModel);


                // TODO: TO BE removed
                //if (device.getBondState() == 12) {
                //    DeviceModel alreadyBondedDevice = new DeviceModel(device);
                //    dPreferences.add(alreadyBondedDevice);
                //    Log.d("alreadyBondedDevice", alreadyBondedDevice.getBondState());
                //   dPreferences.saveList();
                //}

                Log.d(TAG, "ACTION_FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                // Device connected

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                int position = BTArrayAdapter.getDevicePosition(device);
                Log.d("fetched_model", " in ACL_CONNECTED at " + position);
                DeviceModel dm = BTArrayAdapter.getItem(position);
                Log.d("fetched_model", " in ACL_CONNECTED device " + dm.getMacAddress());
                dm.updateState("BOND:BONDED");
                BTArrayAdapter.updateIcon(position);

                // TODO: tu me spremi u bazu
                //DeviceModel connectedModel = new DeviceModel(device);
                //Log.d("connectedDevice", connectedModel.getBondState());

                //dPreferences.add(connectedModel);
                //dPreferences.saveList();

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

                // TODO: tu me izbrisi iz baze

                int position = BTArrayAdapter.getDevicePosition(device);
                Log.d(TAG, "ACL_DISCONNECTED position" + position);
                //if (position != -1) {
                   // DeviceModel dm =  BTArrayAdapter.getItem(position);
                    //dm.updateState("BOND:NONE");
                    //BTArrayAdapter.updateIcon(position);
                //}
                // TODO: indeksiranje se ovdje zna izbrejakti kod updeata
                Log.d(TAG, "ACL_DISCONNECTED");

            }
        }
    };





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myBluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        getActivity().registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        myBluetoothAdapter.startDiscovery();

        // TODO: unregisterReceiver
        dPreferences = new DevicePreferences(getActivity());


        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.srl_main);
        swipeRefreshLayout.setEnabled(false);

        ListView lView = (ListView) getActivity().findViewById(R.id.new_devices_list);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                myBluetoothAdapter.startDiscovery();
                Toast.makeText(getActivity().getApplicationContext(), "Device discovery started, please wait... ",
                        Toast.LENGTH_LONG).show();

                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        myBluetoothAdapter.cancelDiscovery();

                    }
                }, 5000);
            }
        });

        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeRefreshLayout.setEnabled(true);
                else
                    swipeRefreshLayout.setEnabled(false);
            }
        });


        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                DeviceModel dm = BTArrayAdapter.getItem(position);
                Log.d("fetched_model ", dm.getTitle() + " at " + position);

                if (dm.getBondState().equals("BOND:NONE")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Pairing device with " + dm.getTitle() + ".",
                            Toast.LENGTH_LONG).show();
                    dm.pairDevice();
                    dm.updateState("BOND:BONDING");
                    BTArrayAdapter.updateIcon(position);
                }

                if (dm.getBondState().equals("BOND:BONDING")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please wait while we pair with " + dm.getTitle() + ".",
                            Toast.LENGTH_SHORT).show();

                }
                if (dm.getBondState().equals("BOND:BONDED")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unpairing with device " + dm.getTitle() + ".",
                            Toast.LENGTH_SHORT).show();
                    dm.unpairDevice();
                    dm.updateState("BOND:NONE");
                    BTArrayAdapter.updateIcon(position);
                }
            }
        });

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        myBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        myBluetoothAdapter.cancelDiscovery();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        //getActivity().unregisterReceiver(bReceiver);
    }
}
