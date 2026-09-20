package com.twd.gamesetting.light;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.SystemUtils;

public class LightActivity extends AppCompatActivity {

    private LinearLayout ll_light_bright;
    private LinearLayout ll_light_mode;
    private LinearLayout ll_light_speed;
    private TextView tv_cur_light_bright;
    private TextView tv_cur_light_mode;
    private TextView tv_cur_light_speed;

    private String JOYSTICK_LED_BRIGHTNESS = "persist.brightness.joystick.led";//保存摇杆的亮度
    private String JOYSTICK_LED_MODE = "persist.sys.joystick.led";//保存摇杆颜色模式
    private String JOYSTICK_LED_SPEED = "persist.speed.joystick.led";//旋转模式的速度 0慢 1中  2快
    private String path_Brightness = "/sys/leds/brightness"; //0到255  在mode为1时 设定亮度
    private String path_mode = "/sys/leds/mode"; //0关闭，1固定单色，2呼吸灯，3变换色 4炫彩
    private String path_color = "/sys/leds/color"; //0到6   在 mode为1和2时 指定颜色  0红  1黄 2绿  3天空蓝  4蓝 5紫 6淡蓝
    private String path_speed = "/sys/leds/speed";//0慢 1中 2快


    private String[] MODE_NAMES;

    // {path_mode, path_color}，path_color = -1 表示不写
    private static final int[][] MODE_NODE_MAP = {
            {0, -1}, // 0  关闭
            {1,  0}, // 1  长亮_红
            {1,  1}, // 2  长亮_黄
            {1,  2}, // 3  长亮_绿
            {1,  3}, // 4  长亮_天空蓝
            {1,  4}, // 5  长亮_蓝
            {1,  5}, // 6  长亮_紫
            {1,  6}, // 7  长亮_淡蓝
            {2,  0}, // 8  呼吸_红
            {2,  1}, // 9  呼吸_绿
            {2,  2}, // 10 呼吸_蓝
            {2,  3}, // 11 呼吸_紫
            {2,  4}, // 12 呼吸_天空蓝
            {2,  5}, // 13 呼吸_黄
            {2,  6}, // 14 呼吸_淡蓝
            {3, -1}, // 15 呼吸_变色循环
            {4, -1}, // 16 炫彩_旋转
    };

    private static final int MODE_RAINBOW_ROTATE = 16; // 炫彩_旋转
    private AlertDialog unsupportedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        String MODE_OFF = getResources().getString(R.string.light_mode_turn_off);
        String MODE_STATIC_RED = getResources().getString(R.string.light_mode_steady_on_Red);
        String MODE_STATIC_YELLOW = getResources().getString(R.string.light_mode_steady_on_Yellow);
        String MODE_STATIC_GREEN = getResources().getString(R.string.light_mode_steady_on_Green);
        String MODE_STATIC_SKYBLUE = getResources().getString(R.string.light_mode_steady_on_Skyblue);
        String MODE_STATIC_BLUE = getResources().getString(R.string.light_mode_steady_on_Blue);
        String MODE_STATIC_PURPLE = getResources().getString(R.string.light_mode_steady_on_Purple);
        String MODE_STATIC_LIGHTBLUE = getResources().getString(R.string.light_mode_steady_on_Lightblue);
        String MODE_BREATH_RED = getResources().getString(R.string.light_mode_breath_Red);
        String MODE_BREATH_YELLOW = getResources().getString(R.string.light_mode_breath_Yellow);
        String MODE_BREATH_GREEN = getResources().getString(R.string.light_mode_breath_Green);
        String MODE_BREATH_SKYBLUE = getResources().getString(R.string.light_mode_breath_Skyblue);
        String MODE_BREATH_BLUE = getResources().getString(R.string.light_mode_breath_Blue);
        String MODE_BREATH_PURPLE = getResources().getString(R.string.light_mode_breath_Purple);
        String MODE_BREATH_LIGHTBLUE = getResources().getString(R.string.light_mode_breath_Lightblue);
        String MODE_BREATH_COLORCIRCLE = getResources().getString(R.string.light_mode_breath_ColorCycle);
        String MODE_rotating_COLORCIRCLE = getResources().getString(R.string.light_mode_rotating_ColorCycle);
            MODE_NAMES = new String[]{
                    MODE_OFF, MODE_STATIC_RED, MODE_STATIC_YELLOW, MODE_STATIC_GREEN, MODE_STATIC_SKYBLUE,
                    MODE_STATIC_BLUE, MODE_STATIC_PURPLE, MODE_STATIC_LIGHTBLUE, MODE_BREATH_RED, MODE_BREATH_YELLOW,
                    MODE_BREATH_GREEN, MODE_BREATH_SKYBLUE, MODE_BREATH_BLUE, MODE_BREATH_PURPLE, MODE_BREATH_LIGHTBLUE,
                    MODE_BREATH_COLORCIRCLE, MODE_rotating_COLORCIRCLE
            };
        initView();
    }

    private void initView(){
        ll_light_bright = findViewById(R.id.ll_light_bright);
        ll_light_mode = findViewById(R.id.ll_light_mode);
        ll_light_speed = findViewById(R.id.ll_light_rotation_speed);
        tv_cur_light_bright = findViewById(R.id.tv_light_cur_bright);
        tv_cur_light_mode = findViewById(R.id.tv_light_cur_mode);
        tv_cur_light_speed = findViewById(R.id.tv_light_cur_speed);
        initBright(); initMode(); updateSpeedVisibility();

        ll_light_bright.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) { return false; }
                if (!v.isFocused()) { return false; }
                if (isRainbowRotateMode()) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                            || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        showBrightUnsupportedDialog();
                        return true;
                    }
                }
                int currentVal = Integer.parseInt(
                        SystemUtils.getProperty(JOYSTICK_LED_BRIGHTNESS, "0"));
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        // 减亮度，0 再减回到 2
                        currentVal = (currentVal - 1 + 3) % 3;
                        setBrightValue(currentVal);
                        updateBrightText(currentVal);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        // 加亮度，2 再加回到 0
                        currentVal = (currentVal + 1) % 3;
                        setBrightValue(currentVal);
                        updateBrightText(currentVal);
                        return true;
                }
                return false;
            }
        });
        ll_light_mode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) { return false; }
                if (!v.isFocused()) { return false; }
                int currentVal = Integer.parseInt(
                        SystemUtils.getProperty(JOYSTICK_LED_MODE, "0"));
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        // 往前循环，0 再往前回到 16
                        currentVal = (currentVal - 1 + 17) % 17;
                        setModeValue(currentVal);
                        updateModeText(currentVal);
                        updateSpeedVisibility();
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        // 往后循环，16 再往后回到 0
                        currentVal = (currentVal + 1) % 17;
                        setModeValue(currentVal);
                        updateModeText(currentVal);
                        updateSpeedVisibility();
                        return true;
                }
                return false;
            }
        });
        ll_light_speed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) { return false; }
                if (!v.isFocused()) { return false; }
                if (v.getVisibility() != View.VISIBLE) return false;
                int currentVal = Integer.parseInt(
                        SystemUtils.getProperty(JOYSTICK_LED_SPEED, "0"));
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        // 减速度，0 再减回到 2
                        currentVal = (currentVal - 1 + 3) % 3;
                        setSpeedValue(currentVal);
                        updateSpeedText(currentVal);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        // 加速度，2 再加回到 0
                        currentVal = (currentVal + 1) % 3;
                        setSpeedValue(currentVal);
                        updateSpeedText(currentVal);
                        return true;
                }
                return false;
            }
        });
        ll_light_mode.setOnFocusChangeListener((v, hasFocus) -> {
            tv_cur_light_mode.setSelected(hasFocus);
        });
        ll_light_speed.setOnFocusChangeListener((v, hasFocus) -> {
            tv_cur_light_speed.setSelected(hasFocus);
        });
        ll_light_bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRainbowRotateMode()) {
                    showBrightUnsupportedDialog();
                    return;
                }
                startActivity(new Intent(LightActivity.this, BrightSelectActivity.class));
            }
        });

        ll_light_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LightActivity.this, ModeSelectActivity.class));
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        initBright();
        initMode();
        updateSpeedVisibility();
    }
    private void initSpeed() {
        String curSpeed = SystemUtils.getProperty(JOYSTICK_LED_SPEED, "0");
        updateSpeedText(Integer.parseInt(curSpeed));
    }
    private void initBright(){
        String curBright = SystemUtils.getProperty(JOYSTICK_LED_BRIGHTNESS,"0");
        updateBrightText(Integer.parseInt(curBright));
    }
    private void initMode(){
        String curMode = SystemUtils.getProperty(JOYSTICK_LED_MODE,"0");
        updateModeText(Integer.parseInt(curMode));
    }
    private void updateBrightText(int value) {
        switch (value) {
            case 0:
                tv_cur_light_bright.setText(getResources().getString(R.string.light_brightness_low));
                break;
            case 1:
                tv_cur_light_bright.setText(getResources().getString(R.string.light_brightness_medium));
                break;
            case 2:
                tv_cur_light_bright.setText(getResources().getString(R.string.light_brightness_high));
                break;
        }
    }
    private void updateSpeedText(int value) {
        switch (value) {
            case 0:
                tv_cur_light_speed.setText(getResources().getString(R.string.light_rotation_speed_slow));
                break;
            case 1:
                tv_cur_light_speed.setText(getResources().getString(R.string.light_rotation_speed_moderate));
                break;
            case 2:
                tv_cur_light_speed.setText(getResources().getString(R.string.light_rotation_speed_fast));
                break;
        }
    }
    private void updateModeText(int value) {
        if (value >= 0 && value < MODE_NAMES.length) {
            tv_cur_light_mode.setText(MODE_NAMES[value]);
        }
    }

    private void setBrightValue(int value){
        SystemUtils.setProperty(JOYSTICK_LED_BRIGHTNESS, String.valueOf(value));
        int brightness;
        if (value == 0) {brightness = 50;} else if (value == 1) {brightness = 125;} else {brightness = 255;}
        SystemUtils.writeSysNode(path_Brightness, brightness);
    }

    private void setSpeedValue(int value){
        SystemUtils.setProperty(JOYSTICK_LED_SPEED, String.valueOf(value));
        SystemUtils.writeSysNode(path_speed, value);
    }

    private void setModeValue(int value){
        SystemUtils.setProperty(JOYSTICK_LED_MODE, String.valueOf(value));

        if (value < 0 || value >= MODE_NODE_MAP.length) return;

        int[] nodes = MODE_NODE_MAP[value];
        SystemUtils.writeSysNode(path_mode, nodes[0]);
        if (value == 16) {
            SystemUtils.writeSysNode(path_mode, 3); // 先切到呼吸变色循环
            SystemUtils.writeSysNode(path_mode, 4); // 再切到炫彩旋转
            return;
        }

        SystemUtils.writeSysNode(path_mode, nodes[0]);
        if (nodes[1] >= 0) {
            SystemUtils.writeSysNode(path_color, nodes[1]);
        }
    }
    private void updateSpeedVisibility() {
        boolean visible = isRainbowRotateMode();
        ll_light_speed.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            initSpeed();
        }
    }
    private boolean isRainbowRotateMode() {
        int mode = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_MODE, "0"));
        return mode == MODE_RAINBOW_ROTATE;
    }

    private void showBrightUnsupportedDialog() {
        if (unsupportedDialog != null && unsupportedDialog.isShowing()) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_bright_unsupported, null);
        TextView btnConfirm = view.findViewById(R.id.btn_dialog_confirm);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        btnConfirm.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.requestFocus();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        unsupportedDialog = dialog;
        unsupportedDialog.show();
    }

}