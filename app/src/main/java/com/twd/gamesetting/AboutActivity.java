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
import android.view.KeyEvent;
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

    private StringBuilder keySequence = new StringBuilder();
    // 目标应用包名
    private static final String TARGET_PACKAGE_NAME = "com.ty.actionspcbatest";
    // 目标 Activity 全类名
    private static final String TARGET_ACTIVITY_NAME = "com.ty.actionspcbatest.PcbaTestActivity";
    private static final String TRIGGER_SEQUENCE = "UDLRB";
    private boolean hasTriggered = false; // 防止重复触发
    private static final int MAX_SEQUENCE_LENGTH = 20; // 序列最大长度，避免无限增长
    private long lastKeyTime = 0L;
    // 两次按键之间的最大允许间隔（毫秒），超过就清空重来
    private static final long KEY_INTERVAL_TIMEOUT = 1500L;
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

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        String token = null;
        switch (keyCode) {
            case android.view.KeyEvent.KEYCODE_DPAD_UP:    token = "U"; break;
            case android.view.KeyEvent.KEYCODE_DPAD_DOWN:  token = "D"; break;
            case android.view.KeyEvent.KEYCODE_DPAD_LEFT:  token = "L"; break;
            case android.view.KeyEvent.KEYCODE_DPAD_RIGHT: token = "R"; break;
            case android.view.KeyEvent.KEYCODE_BACK:       token = "B"; break;
            default:
                return super.onKeyDown(keyCode, event);
        }

        long now = System.currentTimeMillis();

        // 1. 如果距离上次按键超过阈值，清空序列，重新开始
        if (now - lastKeyTime > KEY_INTERVAL_TIMEOUT) {
            keySequence.setLength(0);
            hasTriggered = false;
        }
        lastKeyTime = now;

        // 2. 追加当前按键
        keySequence.append(token);

        // 3. 防止序列无限增长（只保留最后 N 位）
        if (keySequence.length() > TRIGGER_SEQUENCE.length()) {
            keySequence.delete(0, keySequence.length() - TRIGGER_SEQUENCE.length());
        }

        Log.i(TAG, "onKeyDown: keySequence = " + keySequence
                + ", interval = " + (now - lastKeyTime));

        // 4. 检查是否匹配触发序列
        if (!hasTriggered && keySequence.toString().equals(TRIGGER_SEQUENCE)) {
            hasTriggered = true;
            keySequence.setLength(0);
            openTargetApp();
            return true; // 消费掉这次返回键，避免退出 Activity
        }

        return super.onKeyDown(keyCode, event);
    }

    private void openTargetApp() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new android.content.ComponentName(
                    TARGET_PACKAGE_NAME,
                    TARGET_ACTIVITY_NAME
            ));
            // 如果目标 Activity 没有在同一个任务栈里，或者想以新任务启动，加上这个标志
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            Log.i(TAG, "openTargetApp: 已启动 " + TARGET_PACKAGE_NAME + "/" + TARGET_ACTIVITY_NAME);
        } catch (Exception e) {
            Log.e(TAG, "openTargetApp: 启动失败", e);
            e.printStackTrace();
        }
    }
}