package com.pollinators.buzzshare;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class SenderWifiBroadcastReceiver extends BroadcastReceiver {
    WifiP2pManager wManager;
    WifiP2pManager.Channel wChannel;
    WifiConnectionTesting mainActivity;

    public SenderWifiBroadcastReceiver(WifiP2pManager wManager, WifiP2pManager.Channel wChannel, WifiConnectionTesting mainActivity) {
        this.mainActivity = mainActivity;
        this.wChannel = wChannel;
        this.wManager = wManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "Wifi IS on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Wifi IS off", Toast.LENGTH_SHORT).show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wManager != null) {
                if (mainActivity.checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                wManager.requestPeers(wChannel, mainActivity.peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(wManager!=null){
                NetworkInfo info=intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(info.isConnected()){
                    wManager.requestConnectionInfo(wChannel,mainActivity.connectionInfoListener);
                }else{
                    mainActivity.connectionStatusBox.setText("Not Connected1");
                }
            }

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
