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
        int kernel = Integer.parseInt(
                SystemUtils.getProperty(JOYSTICK_LED_BRIGHTNESS, "50"));
        if (kernel == 50) tvLow.requestFocus();
        else if (kernel == 125) tvMid.requestFocus();
        else tvHigh.requestFocus();
    }

    private void selectBright(int value) {
        int kernel = brightIndexToKernel(value);
        SystemUtils.writeSysNode(path_Brightness, kernel);
        SystemUtils.setProperty(JOYSTICK_LED_BRIGHTNESS, String.valueOf(kernel));
        finish();
    }

    /** UI 索引(0/1/2) → 内核亮度值(50/125/255) */
    private int brightIndexToKernel(int index) {
        if (index == 0) return 50;
        if (index == 1) return 125;
        return 255;
    }
}
