package com.trackfox.android.models;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Sam on 2.12.2014..
 */
public class BLEDeviceModel implements IDevice {

    private final String TAG = "DeviceModel";

    private String title;
    private String macAddress;
    private String bondState;
    private int bondStateCode;

    private BluetoothDevice _device;

    public BLEDeviceModel(String title, String macAddress, int bondState) {
        super();

        if (title != null)
            this.title = title;
        else
            this.title = "Unknown";


        this.macAddress = macAddress;
        this.bondStateCode = bondState;

        switch(bondState) {
            case 10:
                this.bondState = "BOND:NONE";
                break;
            case 11:
                this.bondState = "BOND:BONDING";
                break;
            case 12:
                this.bondState = "BOND:BONDED";
                break;
        }
    }

    public BLEDeviceModel(BluetoothDevice device) {
        this(device.getName(), device.getAddress(), device.getBondState());
    }

    public String getTitle() {
        return this.title;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public String getBondState() { return this.bondState; }

    public int getBondStateCode() { return this.bondStateCode; }


    public void updateState(String state) {
        if (state.equals("BOND:NONE")) {
            this.bondStateCode = 10;
            this.bondState = "BOND:NONE";
        }

        if (state.equals("BOND:BONDING")) {
            this.bondStateCode = 11;
            this.bondState = "BOND:BONDING";
        }

        if (state.equals("BOND:BONDED")) {
            this.bondStateCode = 12;
            this.bondState = "BOND:BONDED";
        }
    }

    public void setDevice(BluetoothDevice device) { this._device = device; }

    public BluetoothDevice getDevice() { return this._device; }

    public void pairDevice() {
        if (this._device != null) {
            try {
                Method m = this._device.getClass()
                        .getMethod("createBond", (Class[]) null);
                m.invoke(this._device, (Object[]) null);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void unpairDevice() {
        try {
            Method m = this._device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(this._device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
