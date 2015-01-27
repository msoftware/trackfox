package com.trackfox.android.activities.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackfox.android.activities.R;
import com.trackfox.android.models.BLEDeviceModel;

import java.util.ArrayList;

/**
 * Created by Sam on 2.12.2014..
 */
public class DeviceListAdapter extends ArrayAdapter<BLEDeviceModel> {


    private final String TAG = "DeviceListAdapter";

    private final Context context;
    private final ArrayList<BLEDeviceModel> itemsArrayList;


    // elements
    private ImageView imgView;
    private TextView labelView;
    private TextView valueView;

    public DeviceListAdapter(Context context) {
        super(context, R.layout.device_list_item);

        this.context = context;
        this.itemsArrayList = new ArrayList<BLEDeviceModel>();

    }

    public boolean exists(BLEDeviceModel item) {
        for (BLEDeviceModel model : this.itemsArrayList) {

            Log.d(TAG, "Checking: " + model.getMacAddress() + " == "
                    +  item.getMacAddress());

            if (model.getMacAddress().equals(item.getMacAddress())) {
                Log.d(TAG, " [+] model == item (" + model.getMacAddress() + " == " + item.getMacAddress());
                return true;
            }

        }
        return false;
    }

    @Override
    public void add(BLEDeviceModel item) {
        boolean itemExists = this.exists(item);

        Log.d(TAG, "itemExists: " + itemExists);
        if (!itemExists) {
            super.add(item);
            this.itemsArrayList.add(item);
            notifyDataSetChanged();
        }
    }

    public int getDevicePosition(BluetoothDevice device) {
        for ( int i = 0; i < this.itemsArrayList.size(); ++i ) {
            if (this.itemsArrayList.get(i).getMacAddress().equals(device.getAddress())) {
                return i;
            }
        }
        return -1;
    }

    public void updateIcon(int position) {

        Log.d("fetched_model", "in update icon at: "  + position);
        if (itemsArrayList.get(position).getBondState().equals("BOND:NONE")) {
            imgView.setImageResource(R.drawable.ic_action_device_bluetooth);
            labelView.setText(itemsArrayList.get(position).getTitle());
        }
        if (itemsArrayList.get(position).getBondState().equals("BOND:BONDING")) {
            imgView.setImageResource(R.drawable.ic_action_editor_insert_link);
            labelView.setText(itemsArrayList.get(position).getTitle() + " (Connecting...)");
        }
        if (itemsArrayList.get(position).getBondState().equals("BOND:BONDED")) {
            imgView.setImageResource(R.drawable.ic_action_device_bluetooth_connected);
            labelView.setText(itemsArrayList.get(position).getTitle() + "  (Paired)");
        }


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.device_list_item, parent, false);

        imgView = (ImageView) rowView.findViewById(R.id.chkItemImage);
        labelView = (TextView) rowView.findViewById(R.id.label);
        valueView = (TextView) rowView.findViewById(R.id.value);
        //TextView bondingState = (TextView) rowView.findViewById(R.id.bonding_state);

        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getMacAddress());


        this.updateIcon(position);


        //bondingState.setText("" + itemsArrayList.get(position).getBondState());

        Log.d(TAG, "itemsArrayList: " + itemsArrayList.size());

        return rowView;
    }


}
