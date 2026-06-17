package com.twd.gamesetting;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout LL_language;  TextView tv_cur_lang;
    TextView tv_wifi;
    TextView tv_bluetooth;
    TextView tv_about;
    LinearLayout LL_bright; TextView tv_cur_bright;

    // 亮度范围 50~100，步长10
    private final int MIN_BRIGHT = 50;
    private final int MAX_BRIGHT = 100;
    private final int STEP = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回MainActivity时更新当前语言显示
        updateCurrentLanguage();
    }
    private void initView(){
        LL_language = findViewById(R.id.ll_language);  tv_cur_lang =findViewById(R.id.tv_cur_lang);
        tv_wifi = findViewById(R.id.tv_wifi);
        tv_bluetooth = findViewById(R.id.tv_bluetooth);
        tv_about = findViewById(R.id.tv_about);
        LL_bright = findViewById(R.id.ll_bright); tv_cur_bright = findViewById(R.id.tv_cur_bright);

        // 初始化读取亮度
        readCurrentBrightness();

        LL_bright.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {return false;}
                if (!v.isFocused()) {return false;}
                int currentVal = getCurrentBrightValue();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        // 左箭头：亮度降低
                        currentVal -= STEP;
                        if (currentVal < MIN_BRIGHT) currentVal = MIN_BRIGHT;
                        setBrightness(currentVal);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        // 右箭头：亮度升高
                        currentVal += STEP;
                        if (currentVal > MAX_BRIGHT) currentVal = MAX_BRIGHT;
                        setBrightness(currentVal);
                        return true;
                }return false;}});
        LL_language.setOnClickListener(this::onClick);
        tv_wifi.setOnClickListener(this::onClick);
        tv_bluetooth.setOnClickListener(this::onClick);
        tv_about.setOnClickListener(this::onClick);

        LL_language.requestFocus();
        updateCurrentLanguage();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.ll_language){
            intent = new Intent(this,LanguageActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_wifi) {
            intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings","com.android.settings.wifi.WifiSettings"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_bluetooth) {
            intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_about) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    private void updateCurrentLanguage() {
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguage = currentLocale.getLanguage() + "_" + currentLocale.getCountry();

        Log.i("Language", "当前系统语言: " + currentLanguage);

        // 根据语言代码显示对应的语言名称（只支持中文和英文）
        if (currentLanguage.contains("zh_CN")) {
            tv_cur_lang.setText("简体中文");
        } else if (currentLanguage.contains("zh_TW")) {
            tv_cur_lang.setText("繁體中文");
        } else if (currentLanguage.equals("en_US")) {
            tv_cur_lang.setText("English");
        }else if (currentLanguage.equals("fr_FR")) {
            tv_cur_lang.setText("Français");
        }else if (currentLanguage.equals("de_DE")) {
            tv_cur_lang.setText("Deutsch");
        }else if (currentLanguage.equals("ru_RU")) {
            tv_cur_lang.setText("Pусский");
        }else if (currentLanguage.equals("ja_JP")) {
            tv_cur_lang.setText("日本語");
        }else if (currentLanguage.equals("ko_KR")) {
            tv_cur_lang.setText("한국어");
        } else if (currentLanguage.equals("es_ES")) {
            tv_cur_lang.setText("Español");
        } else {
            // 默认显示英文
            tv_cur_lang.setText("English");
        }
    }

    /**
     * 读取系统当前屏幕亮度 0~255，转换为百分比0~100
     */
    private void readCurrentBrightness() {
        ContentResolver cr = getContentResolver();
        int sysBright;
        try {
            sysBright = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            // 无设置时默认128（50%）
            sysBright = 128;
        }
        // 转百分比：0~255 → 0~100
        int percent = Math.round(sysBright * 100f / 255f);
        // 限制在50~100区间显示
        percent = Math.max(MIN_BRIGHT, Math.min(MAX_BRIGHT, percent));
        tv_cur_bright.setText(percent + "%");
    }

    /**
     * 获取当前UI显示的亮度百分比数值
     */
    private int getCurrentBrightValue() {
        String text = tv_cur_bright.getText().toString().replace("%", "");
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return 50;
        }
    }

    /**
     * 设置亮度：百分比50~100 转系统0~255并保存
     * @param percent 50~100
     */
    private void setBrightness(int percent) {
        // 更新UI显示
        tv_cur_bright.setText(percent + "%");
        // 百分比转系统标准值 0~255
        int sysVal = Math.round(percent * 255f / 100f);
        // 写入系统设置
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, sysVal);
        // 同步实时生效当前页面亮度
        android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = sysVal / 255f;
        getWindow().setAttributes(lp);
    }
}