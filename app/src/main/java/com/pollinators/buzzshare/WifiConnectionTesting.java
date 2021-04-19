//package com.pollinators.buzzshare;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.InetAddresses;
//import android.net.wifi.WifiManager;
//import android.net.wifi.p2p.WifiP2pConfig;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pDeviceList;
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.net.InetAddress;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WifiConnectionTesting extends AppCompatActivity {
//    Button btnOff, btnDiscover, btnSend;
//    ListView listView;
//    TextView readMessageBox, connectionStatusBox;
//    EditText writeMessage;
//    WifiManager wifiManager;
//    WifiP2pManager wifiP2pManager;
//    WifiP2pManager.Channel wchannel;
//    BroadcastReceiver wifiBroadcastReceiver;
//    IntentFilter wifiIntentFilter;
//    List<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();
//    String deviceNameArray[];
//    WifiP2pDevice deviceArray[];
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wifi_connection_testing);
//        initialWork();
//        listeners();
//    }
//
//    void listeners() {
//        btnOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                startActivityForResult(intent, 1);
//            }
//        });
//        btnDiscover.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View v) {
//                wifiP2pManager.discoverPeers(wchannel, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        connectionStatusBox.setText("Discovery Started");
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                        connectionStatusBox.setText("Discovery Failed");
//                    }
//                });
//            }
//        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final WifiP2pDevice wifiP2pDevice = deviceArray[position];
//                WifiP2pConfig config = new WifiP2pConfig();
//                config.deviceAddress = wifiP2pDevice.deviceAddress;
//                wifiP2pManager.connect(wchannel, config, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        connectionStatusBox.setText("Connected to: "+wifiP2pDevice.deviceAddress);
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                        connectionStatusBox.setText("No Device found");
//                    }
//                });
//
//            }
//        });
//    }
//    void initialWork(){
//        listView=findViewById(R.id.peerListView);
//        btnOff=findViewById(R.id.onOff);
//        btnDiscover=findViewById(R.id.discover);
//        readMessageBox=findViewById(R.id.readMsg);
//        connectionStatusBox=findViewById(R.id.connectionStatus);
//        writeMessage=findViewById(R.id.writeMsg);
//        wifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiP2pManager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
//        wchannel=wifiP2pManager.initialize(this,getMainLooper(),null);
//        wifiBroadcastReceiver=new SenderWifiBroadcastReceiver(wifiP2pManager,wchannel,this);
//        wifiIntentFilter=new IntentFilter();
//        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//        wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//
//    }
//    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
//        @Override
//        public void onConnectionInfoAvailable(WifiP2pInfo info) {
//            final InetAddress groupOWnerAddres=info.groupOwnerAddress;
//            if(info.groupFormed && info.isGroupOwner){
//                connectionStatusBox.setText("Is HOST");
//            }else{
//                connectionStatusBox.setText("CLIENT");
//            }
//        }
//    };
//    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
//        @Override
//        public void onPeersAvailable(WifiP2pDeviceList peersList) {
//            if(!peersList.getDeviceList().equals(deviceList)){
//                System.out.println("PEERS LIST IS NOT EQUAL TO DEVICE LIST\n\n");
//                deviceList.clear();
//                deviceList.addAll(peersList.getDeviceList());
//                deviceNameArray=new String[deviceList.size()];
//                deviceArray=new WifiP2pDevice[deviceList.size()];
//                int i=0;
//                for(WifiP2pDevice dd:deviceList){
//                    deviceNameArray[i]=dd.deviceName;
//                    deviceArray[i++]=dd;
//                }
//                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1);
//                listView.setAdapter(adapter);
//            }
//            if(deviceList.size()==0){
//                connectionStatusBox.setText("NO DEVICE FOUND");
//                return;
//            }
//        }
//
//    };
//    public void onResume(){
//        super.onResume();
//        registerReceiver(wifiBroadcastReceiver,wifiIntentFilter);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(wifiBroadcastReceiver);
//    }
//}