package com.twd.gamesetting;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private String TAG = "AboutActivity";
    TextView tv_device_name;
    TextView tv_software_version;
    TextView tv_wifi_mac;
    TextView tv_bluetooth_mac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_software_version = findViewById(R.id.tv_software_version);
        tv_wifi_mac = findViewById(R.id.tv_wifi_mac);
        tv_bluetooth_mac = findViewById(R.id.tv_bluetooth_mac);
        setDeviceName();
        setSoftwareNo();
        setMACAddressWifi();
        setMACAddressBluetooth();
    }


    /*
     * 获取设备名称*/
    private void setDeviceName(){
        String deviceName = Build.MODEL;
        tv_device_name.setText(deviceName);
        Log.i(TAG, "setDeviceName: --------tv_device_name = " + deviceName);
    }


    /*
     * 获取软件版本*/
    private void setSoftwareNo(){
        String softwareNo = SystemPropertiesUtils.getProperty("ro.twd.version","v1.0");
        tv_software_version.setText(softwareNo);
        Log.i(TAG, "setSoftwareNO: -----software = " + softwareNo);
    }

    /*
     * 获取wifi的mac地址*/
    private void setMACAddressWifi(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if (macAddress != null){
            macAddress = macAddress.toUpperCase();
        }
        tv_wifi_mac.setText(macAddress);
        Log.i(TAG, "setMACAddressWifi: ---------macAddress_wifi = " + macAddress);
    }

    /*
     * 获取蓝牙的mac地址*/
    private void setMACAddressBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null){
            String macAddress = bluetoothAdapter.getAddress();
            if (macAddress != null){
                macAddress = macAddress.toUpperCase();
                Log.i(TAG, "setMACAddressBluetooth: --------bluetooth = " + macAddress);
            }
            tv_bluetooth_mac.setText(macAddress);
        }
    }
}