package com.twd.gamesetting.light;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.SystemUtils;

public class SpeedSelectActivity extends AppCompatActivity {

    private String JOYSTICK_LED_SPEED = "persist.speed.joystick.led";//旋转模式的速度 0慢 1中  2快
    private String path_speed = "/sys/leds/speed";//0慢 1中 2快
    private TextView tvLow,tvMid,tvHigh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_select);

        tvLow = findViewById(R.id.tv_speed_low);
        tvMid = findViewById(R.id.tv_speed_mid);
        tvHigh = findViewById(R.id.tv_speed_high);

        tvLow.setOnClickListener(v -> setSpeedValue(0));
        tvMid.setOnClickListener(v -> setSpeedValue(1));
        tvHigh.setOnClickListener(v -> setSpeedValue(2));

        int kernel = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_SPEED,"0"));
        if (kernel==0) tvLow.requestFocus();
        else if (kernel == 1) tvMid.requestFocus();
        else tvHigh.requestFocus();
    }

    private void setSpeedValue(int value){
        SystemUtils.setProperty(JOYSTICK_LED_SPEED, String.valueOf(value));
        SystemUtils.writeSysNode(path_speed, value);
        finish();
    }
}
