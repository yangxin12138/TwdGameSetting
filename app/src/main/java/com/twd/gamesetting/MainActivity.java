package com.twd.gamesetting;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.twd.gamesetting.utils.GlobalSoundSwitchUtil;
import com.twd.gamesetting.utils.SoundHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout LL_language;  TextView tv_cur_lang;
    TextView tv_wifi;
    TextView tv_bluetooth;
    LinearLayout LL_bright; TextView tv_cur_bright;
    LinearLayout LL_sound;
    LinearLayout LL_about;
    TextView tv_sound_status;
    private SoundHelper soundHelper;
    // 防焦点音效频繁触发
    private long lastFocusSoundTime = 0;

    // 亮度范围 50~100，步长10
    private final int MIN_BRIGHT = 10;
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

        soundHelper = SoundHelper.getInstance(this);
        LL_sound = findViewById(R.id.ll_sound);
        tv_sound_status = findViewById(R.id.tv_sound_status);

        LL_language = findViewById(R.id.ll_language);  tv_cur_lang =findViewById(R.id.tv_cur_lang);
        tv_wifi = findViewById(R.id.tv_wifi);
        tv_bluetooth = findViewById(R.id.tv_bluetooth);
        LL_about = findViewById(R.id.ll_about);
        LL_bright = findViewById(R.id.ll_bright); tv_cur_bright = findViewById(R.id.tv_cur_bright);

        // 初始化读取亮度
        readCurrentBrightness();

        boolean soundOpen = GlobalSoundSwitchUtil.isSoundEffectOpen(getContentResolver());
        updateSoundStatusText(soundOpen);




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
                        //soundHelper.playSelect();
                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        // 右箭头：亮度升高
                        currentVal += STEP;
                        if (currentVal > MAX_BRIGHT) currentVal = MAX_BRIGHT;
                        setBrightness(currentVal);
                        //soundHelper.playSelect();
                        return true;
                }return false;}});
        LL_sound.setOnClickListener(v -> {
            //soundHelper.playConfirm(); // 按下OK播放确认音效
            boolean current = GlobalSoundSwitchUtil.isSoundEffectOpen(getContentResolver());
            boolean newState = !current;
            // 保存
            GlobalSoundSwitchUtil.setSoundEffect(getContentResolver(), newState);
            // 更新界面文字
            updateSoundStatusText(newState);
        });
        LL_language.setOnClickListener(this::onClick);
        tv_wifi.setOnClickListener(this::onClick);
        tv_bluetooth.setOnClickListener(this::onClick);
        LL_about.setOnClickListener(this::onClick);
        setFocusListener(LL_language);
        setFocusListener(LL_bright);
        setFocusListener(tv_wifi);
        setFocusListener(tv_bluetooth);
        setFocusListener(LL_about);
        setFocusListener(LL_sound);
        LL_language.requestFocus();
        updateCurrentLanguage();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        //soundHelper.playConfirm();
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
        } else if (view.getId() == R.id.ll_about) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //soundHelper.playSelect();
        super.onBackPressed();
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
        } else if (currentLanguage.equals("ar_SA")) {
            tv_cur_lang.setText("Arabic");
        } else if (currentLanguage.equals("bg_BG")) {
            tv_cur_lang.setText("Български");
        } else if (currentLanguage.equals("cs_CZ")) {
            tv_cur_lang.setText("Čeština");
        } else if (currentLanguage.equals("da_DK")) {
            tv_cur_lang.setText("Dansk");
        } else if (currentLanguage.equals("el_GR")) {
            tv_cur_lang.setText("Ελληνικά");
        } else if (currentLanguage.equals("fa_IR")) {
            tv_cur_lang.setText("Farsi");
        } else if (currentLanguage.equals("fi_FI")) {
            tv_cur_lang.setText("Suomi");
        } else if (currentLanguage.equals("fil_PH")) {
            tv_cur_lang.setText("Filipino");
        } else if (currentLanguage.equals("hi_IN")) {
            tv_cur_lang.setText("हिंदी");
        } else if (currentLanguage.equals("hr_HR")) {
            tv_cur_lang.setText("Hrvatski");
        } else if (currentLanguage.equals("hu_HU")) {
            tv_cur_lang.setText("Magyar");
        } else if (currentLanguage.equals("in_ID")) {
            tv_cur_lang.setText("Bahasa Indonesia");
        } else if (currentLanguage.equals("it_IT")) {
            tv_cur_lang.setText("Italiano");
        } else if (currentLanguage.equals("iw_IL")) {
            tv_cur_lang.setText("Hebrew");
        }  else if (currentLanguage.equals("lt_LT")) {
            tv_cur_lang.setText("Lietuvių");
        }  else if (currentLanguage.equals("lv_LT")) {
            tv_cur_lang.setText("Latviski");
        }  else if (currentLanguage.equals("ms_MY")) {
            tv_cur_lang.setText("Bahasa Melayu");
        }  else if (currentLanguage.equals("nb_NO")) {
            tv_cur_lang.setText("Norsk bokmål");
        }  else if (currentLanguage.equals("nl_NL")) {
            tv_cur_lang.setText("Nederlands");
        }  else if (currentLanguage.equals("pl_PL")) {
            tv_cur_lang.setText("Polski");
        }  else if (currentLanguage.equals("pt_PT")) {
            tv_cur_lang.setText("Português");
        }  else if (currentLanguage.equals("ro_RO")) {
            tv_cur_lang.setText("Română");
        }  else if (currentLanguage.equals("sk_SK")) {
            tv_cur_lang.setText("Slovensky");
        }  else if (currentLanguage.equals("sl_SI")) {
            tv_cur_lang.setText("Slovenski jezik");
        }  else if (currentLanguage.equals("sv_SE")) {
            tv_cur_lang.setText("Svenska");
        }  else if (currentLanguage.equals("th_TH")) {
            tv_cur_lang.setText("ไทย");
        }  else if (currentLanguage.equals("tr_TR")) {
            tv_cur_lang.setText("Türkçe");
        }  else if (currentLanguage.equals("uk_UA")) {
            tv_cur_lang.setText("Українська");
        }  else if (currentLanguage.equals("vi_VN")) {
            tv_cur_lang.setText("Tiếng Việt");
        }  else {
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

    private void setFocusListener(View view){
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                long now = System.currentTimeMillis();
                if(now - lastFocusSoundTime > 120){
                    //soundHelper.playSelect();
                    lastFocusSoundTime = now;
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateSoundStatusText(boolean isOpen){
        if(isOpen){
            tv_sound_status.setText(R.string.status_on);
        }else{
            tv_sound_status.setText(R.string.status_off);
        }
    }
}