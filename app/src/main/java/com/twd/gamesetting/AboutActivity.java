package com.twd.gamesetting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.gamesetting.utils.SoundHelper;

public class AboutActivity extends AppCompatActivity {

    private String TAG = "AboutActivity";
    TextView tv_device_name;
    TextView tv_software_version;
    TextView tv_wifi_mac;
    TextView tv_bluetooth_mac;
    TextView factory_tv;
    private SoundHelper soundHelper;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        soundHelper = SoundHelper.getInstance(this);
        tv_device_name = findViewById(R.id.tv_device_name);
        tv_software_version = findViewById(R.id.tv_software_version);
        tv_wifi_mac = findViewById(R.id.tv_wifi_mac);
        tv_bluetooth_mac = findViewById(R.id.tv_bluetooth_mac);
        factory_tv = findViewById(R.id.factory_tv);
        setDeviceName();
        setSoftwareNo();
        setMACAddressWifi();
        setMACAddressBluetooth();
        context = this;
        factory_tv.requestFocus();
        factory_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFactoryDialog();
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //soundHelper.playSelect();
        super.onBackPressed();
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

    private void showFactoryDialog(){
        Dialog FactoryDialog = new Dialog(this,R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.factory_dialog, null);
        FactoryDialog.setContentView(dialogView);
        dialogView.setPadding(50,0,50,50);

        final TextView factoryTitle = dialogView.findViewById(R.id.factory_title);
        final LinearLayout okBT = dialogView.findViewById(R.id.factory_ok_bt);
        final LinearLayout cancelBT = dialogView.findViewById(R.id.factory_cancel_bt);
        factoryTitle.setText(getString(R.string.factory_dialog_title));
        FactoryDialog.show();

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: factory ok");
                try {
                    startFactoryDefault(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: factory cancel");
                FactoryDialog.dismiss();
            }
        });
    }

    public static void startFactoryDefault(Context context) throws Exception {
        if (Build.VERSION.SDK_INT < 26) {
            context.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
        } else {
            Intent intent = new Intent("android.intent.action.FACTORY_RESET");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setPackage("android");
            context.sendBroadcast(intent);
        }
    }
}