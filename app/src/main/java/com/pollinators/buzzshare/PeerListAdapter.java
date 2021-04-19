package com.pollinators.buzzshare;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class PeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
    private ArrayList<WifiP2pDevice> deviceList;

    public PeerListAdapter(Context context, int resource, ArrayList<WifiP2pDevice> objects) {
        super(context, resource, objects);
        this.deviceList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.peer_list_item, null);
        }

        WifiP2pDevice device = deviceList.get(position);
        if (device != null) {
            TextView tvDeviceName = (TextView) v.findViewById(R.id.tvDeviceName);
            TextView tvDeviceAddress = (TextView) v.findViewById(R.id.tvDeviceAddress);

            if (tvDeviceName != null) {
                tvDeviceName.setText(device.deviceName);
            }
            if (tvDeviceAddress != null) {
                tvDeviceAddress.setText(device.deviceAddress);
            }
        }

        return v;
    }
}

