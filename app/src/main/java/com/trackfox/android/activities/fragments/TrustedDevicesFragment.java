package com.trackfox.android.activities.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.trackfox.android.activities.R;
import com.trackfox.android.activities.adapters.DeviceListAdapter;
import com.trackfox.android.models.DeviceModel;
import com.trackfox.android.utils.PairedCache;

import java.util.Set;

/**
 * Created by Sam on 1.12.2014..
 */
public class TrustedDevicesFragment extends Fragment {


    private String TAG = "TrustedDevicesFragment";
    private ListView deviceListView;
    private DeviceListAdapter BTArrayAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private android.bluetooth.BluetoothAdapter myBluetoothAdapter;

    private Set<BluetoothDevice> pairedDevices;
    //private DevicePreferences devicePreferences;

    private PairedCache pairedCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_trusted_devices, container, false);


        deviceListView = (ListView) rootView.findViewById(R.id.trusted_devices_list);
        BTArrayAdapter = new DeviceListAdapter(
                getActivity().getApplicationContext());
        //new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(BTArrayAdapter);

        //devicePreferences = new DevicePreferences(getActivity());
        pairedCache = new PairedCache(getActivity().getApplicationContext());

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myBluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.trusted_devices);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        pairedDevices = myBluetoothAdapter.getBondedDevices();

        //for (BluetoothDevice device : pairedDevices)
         //   BTArrayAdapter.add(new DeviceModel(device));

        Log.d(TAG, "pairedCache size: " + pairedCache.getList().size());
        for (DeviceModel device: pairedCache.getList()) {
            if (device.getBondStateCode() != 12) {
                device.updateState("BOND:BONDED");
            }
            Log.d(TAG, device.getTitle());
            Log.d(TAG, device.getBondState());
            Log.d(TAG, "" + device.getBondStateCode());
            BTArrayAdapter.add(device);
        }
    }
}
