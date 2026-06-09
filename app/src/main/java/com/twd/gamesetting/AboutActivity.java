package com.twd.gamesetting;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private String TAG = "AboutActivity";
    TextView tv_android_version;
    TextView tv_device_code;
    TextView tv_software_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv_android_version = findViewById(R.id.tv_android_version);
        tv_device_code = findViewById(R.id.tv_device_code);
        tv_software_version = findViewById(R.id.tv_software_version);

        setAndroidVersion();
        setMachineNo();
        setSoftwareNo();
    }


    /*
     * 获取设备号*/
    private void setMachineNo(){
        String machineNo = Build.MODEL;
        tv_device_code.setText(machineNo);
        Log.i(TAG, "getMachineNO: --------machineNo = " + machineNo);
    }

    /*
     * 获取安卓版本号*/
    private void setAndroidVersion(){
        String version;
        version = Build.VERSION.RELEASE;
        tv_android_version.setText(version);
        Log.i(TAG, "setAndroidVersion: ----------version = " + version);
    }

    /*
     * 获取软件版本*/
    private void setSoftwareNo(){
        String softwareNo = Build.VERSION.INCREMENTAL;
        tv_software_version.setText(softwareNo);
        Log.i(TAG, "setSoftwareNO: -----software = " + softwareNo);
    }
}