package com.pollinators.buzzshare;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class SenderWiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private PeerChooserActivity peerChooserActivity;

    public SenderWiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, Channel channel, PeerChooserActivity peerChooserActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.peerChooserActivity = peerChooserActivity;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        System.out.println("receiver: onreceive");
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("receiver: WIFI_P2P_STATE_ENABLED");
            } else {
                System.out.println("receiver: WIFI_P2P_STATE_DISABLED");
                // Disable WiFi hotspot and enable WiFi
                WifiManager wifiManager = (WifiManager) peerChooserActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiApControl wifiApControl = WifiApControl.getApControl(wifiManager);
                boolean result;
                try
                {
                    Intent intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivityForResult(peerChooserActivity,intent1,1,null);
                    Method enableWifi = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    String ssid  =   "abc"; //your SSID
                    String pass  =   "1234"; // your Password
                    WifiConfiguration myConfig =  new WifiConfiguration();
                    myConfig.SSID = ssid;
                    myConfig.preSharedKey  = pass ;
                    myConfig.status = WifiConfiguration.Status.ENABLED;
                    myConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    myConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    myConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    myConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    myConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    myConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    myConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    result = (Boolean) enableWifi.invoke(wifiManager, myConfig, true);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    result = false;
                }wifiManager.setWifiEnabled(true);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_PEERS_CHANGED_ACTION");
            if (wifiP2pManager != null) {
                MainActivity mainActivity=new MainActivity();
                wifiP2pManager.requestPeers(channel, new PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        if (wifiP2pDeviceList.getDeviceList().size() == 0) {
                            peerChooserActivity.tvDevicesStatus.setText("No devices found");
                            peerChooserActivity.tvDevicesStatus.setVisibility(View.VISIBLE);
                        } else {
                            peerChooserActivity.tvDevicesStatus.setText("");
                            peerChooserActivity.tvDevicesStatus.setVisibility(View.INVISIBLE);
                        }
                        peerChooserActivity.peerListAdapter = new PeerListAdapter(peerChooserActivity.getApplicationContext(), R.layout.peer_list_item, new ArrayList<>(wifiP2pDeviceList.getDeviceList()));
                        peerChooserActivity.lvPeers.setAdapter(peerChooserActivity.peerListAdapter);
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_CONNECTION_CHANGED_ACTION");
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                System.out.println("connected");
                wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        System.out.println("wifiP2pInfo.groupFormed: " + wifiP2pInfo.groupFormed);
                        System.out.println("wifiP2pInfo.groupOwnerAddress == null: " + (wifiP2pInfo.groupOwnerAddress == null));
                        try {
                            System.out.println("sleep for 1sec");
                            Thread.sleep(1000);
                            System.out.println("slept for 1sec");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (wifiP2pInfo.groupOwnerAddress != null) {
                            System.out.println("groupOwnerAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
                            Intent senderActivityIntent = new Intent(context, SenderActivity.class);

                            Intent otherAppIntent = peerChooserActivity.getIntent();
                            if (otherAppIntent.getAction() != null) {
                                if (peerChooserActivity.getIntent().getAction().equals(Intent.ACTION_SEND)) {
                                    ArrayList<File> filesToBeSent = new ArrayList<>();
                                    System.out.println(otherAppIntent.getParcelableExtra(Intent.EXTRA_STREAM));
                                    filesToBeSent.add(new File((getRealPathFromURI(context, (Uri) otherAppIntent.getParcelableExtra(Intent.EXTRA_STREAM)))));
                                    senderActivityIntent.putExtra("filesToBeSent", filesToBeSent);
                                } else if (otherAppIntent.getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
                                    ArrayList<File> filesToBeSent = new ArrayList<>();
                                    System.out.println(otherAppIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM));
                                    ArrayList<Uri> uriArrayList = otherAppIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                                    for (int i = 0; i < uriArrayList.size(); ++i) {
                                        System.out.println(getRealPathFromURI(context, uriArrayList.get(i)));
                                        filesToBeSent.add(new File(getRealPathFromURI(context, uriArrayList.get(i))));
                                    }
                                    senderActivityIntent.putExtra("filesToBeSent", filesToBeSent);
                                }
                            } else {
                                senderActivityIntent.putExtra("filesToBeSent", peerChooserActivity.getIntent().getSerializableExtra("filesToBeSent"));
                            }


                            senderActivityIntent.putExtra("serverIP", wifiP2pInfo.groupOwnerAddress.getHostAddress());
                            context.startActivity(senderActivityIntent);
                        }
                    }
                });
            } else {
                System.out.println("not connected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            System.out.println("receiver: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
