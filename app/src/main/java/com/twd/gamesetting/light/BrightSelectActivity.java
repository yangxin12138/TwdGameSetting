package com.twd.gamesetting.light;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.SystemUtils;

public class BrightSelectActivity extends AppCompatActivity {

    private static final String JOYSTICK_LED_BRIGHTNESS = "persist.brightness.joystick.led";
    private static final String path_Brightness = "/sys/leds/brightness";
    private TextView tvLow, tvMid, tvHigh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_select);

        tvLow = findViewById(R.id.tv_low);
        tvMid = findViewById(R.id.tv_mid);
        tvHigh = findViewById(R.id.tv_high);

        tvLow.setOnClickListener(v -> selectBright(0));
        tvMid.setOnClickListener(v -> selectBright(1));
        tvHigh.setOnClickListener(v -> selectBright(2));

        // 默认焦点给当前值
        int cur = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_BRIGHTNESS, "0"));
        if (cur == 0) tvLow.requestFocus();
        else if (cur == 1) tvMid.requestFocus();
        else tvHigh.requestFocus();
    }

    private void selectBright(int value) {
        SystemUtils.setProperty(JOYSTICK_LED_BRIGHTNESS, String.valueOf(value));
        int brightness;
        if (value == 0) {brightness = 50;} else if (value == 1) {brightness = 125;} else {brightness = 255;}
        SystemUtils.writeSysNode(path_Brightness, brightness);
        finish();
    }
}
