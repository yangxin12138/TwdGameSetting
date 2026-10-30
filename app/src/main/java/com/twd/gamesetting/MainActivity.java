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
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.twd.gamesetting.bean.SettingItem;
import com.twd.gamesetting.light.LightActivity;
import com.twd.gamesetting.time.TimeActivity;
import com.twd.gamesetting.utils.GlobalSoundSwitchUtil;
import com.twd.gamesetting.utils.SoundHelper;
import com.twd.gamesetting.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 动作常量定义（截图3报错是因为缺这些）
    public static final int ACTION_LANGUAGE = 1;
    public static final int ACTION_SOUND    = 2;
    public static final int ACTION_BRIGHT   = 3;
    public static final int ACTION_WIFI     = 4;
    public static final int ACTION_BLUETOOTH= 5;
    public static final int ACTION_LIGHT    = 6;
    public static final int ACTION_TIME     = 7;
    public static final int ACTION_ABOUT    = 8;

    private ListView listView;
    private SettingItemAdapter adapter;
    private List<SettingItem> items = new ArrayList<>();

    private SoundHelper soundHelper;
    private long lastFocusSoundTime = 0;

    // 亮度范围
    private final int MIN_BRIGHT = 10;
    private final int MAX_BRIGHT = 100;
    private final int STEP = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 确保你的xml已经改成了ListView版本
        listView = findViewById(R.id.list_settings);
        soundHelper = SoundHelper.getInstance(this);

        // 1. 构建数据
        buildItems();

        // 2. 设置 Adapter
        adapter = new SettingItemAdapter(this, items);
        listView.setAdapter(adapter);

        // 3. 焦点改变监听（播放按键音）
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setFocusedItem(position);
                long now = System.currentTimeMillis();
                if (now - lastFocusSoundTime > 120) {
                    // soundHelper.playSelect();
                    lastFocusSoundTime = now;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 4. 点击事件监听（OK键）
        listView.setOnItemClickListener((parent, view, position, id) -> {
            SettingItem item = items.get(position);
            handleItemClick(item);
        });

        // 5. 特殊按键监听：亮度项左右键（截图1报错是因为缺变量）
        listView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;

            int pos = listView.getSelectedItemPosition();
            if (pos < 0 || pos >= items.size()) return false;

            SettingItem item = items.get(pos);
            if (item.getType() != SettingItem.TYPE_BRIGHT) return false;

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                int val = Math.max(MIN_BRIGHT, getCurrentBrightValue() - STEP);
                setBrightness(val);
                updateBrightSubTitle(val);
                adapter.notifyDataSetChanged();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int val = Math.min(MAX_BRIGHT, getCurrentBrightValue() + STEP);
                setBrightness(val);
                updateBrightSubTitle(val);
                adapter.notifyDataSetChanged();
                return true;
            }
            return false;
        });

        listView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 截图2报错是因为没有 updateCurrentLanguage() 和 readCurrentBrightness() 的实现
        updateCurrentLanguage();
        readCurrentBrightness();

        boolean soundOpen = GlobalSoundSwitchUtil.isSoundEffectOpen(getContentResolver());
        updateSoundSubTitle(soundOpen);
        adapter.notifyDataSetChanged();
        updateLightVisibility();
    }

    /**
     * 构建数据列表
     */
    private void buildItems() {
        items.clear();
        items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.index_language, ACTION_LANGUAGE));
        items.add(new SettingItem(SettingItem.TYPE_SOUND,  R.string.index_sound,    ACTION_SOUND));
        items.add(new SettingItem(SettingItem.TYPE_BRIGHT, R.string.index_bright,   ACTION_BRIGHT));
        items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.index_Wifi,     ACTION_WIFI));
        items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.index_Bluetooth,ACTION_BLUETOOTH));

        // 动态判断是否显示灯光
        if (SystemUtils.getProperty("ro.sys.joystick.led", "0").equals("1")) {
            items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.light, ACTION_LIGHT));
        }

        items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.time_setting,   ACTION_TIME));
        items.add(new SettingItem(SettingItem.TYPE_NORMAL, R.string.index_About,    ACTION_ABOUT));
    }

    /**
     * 处理点击事件
     */
    private void handleItemClick(SettingItem item) {
        Intent intent;
        switch (item.getActionId()) {
            case ACTION_LANGUAGE:
                intent = new Intent(this, LanguageActivity.class);
                startActivity(intent);
                break;
            case ACTION_SOUND:
                boolean current = GlobalSoundSwitchUtil.isSoundEffectOpen(getContentResolver());
                boolean newState = !current;
                GlobalSoundSwitchUtil.setSoundEffect(getContentResolver(), newState);
                updateSoundSubTitle(newState);
                adapter.notifyDataSetChanged();
                break;
            case ACTION_WIFI:
                intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case ACTION_BLUETOOTH:
                intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case ACTION_LIGHT:
                intent = new Intent(this, LightActivity.class);
                startActivity(intent);
                break;
            case ACTION_TIME:
                intent = new Intent(this, TimeActivity.class);
                startActivity(intent);
                break;
            case ACTION_ABOUT:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case ACTION_BRIGHT:
                 intent = new Intent(this, BrightnessActivity.class);
                 startActivity(intent);
                break;
        }
    }

    /**
     * 动态显示/隐藏灯光项（如果需要在 onResume 动态更新的话）
     */
    private void updateLightVisibility() {
        // 因为我们在 buildItems 里已经判断了，这里如果不需要动态刷新，可以留空。
        // 若需要动态处理，需要遍历 items 更改 visible 并重建列表。
    }

    // ==========================================
    // 副标题更新方法
    // ==========================================

    private void updateLanguageSubTitle() {
        Locale currentLocale = getResources().getConfiguration().locale;
        String code = currentLocale.getLanguage() + "_" + currentLocale.getCountry();
        String name = getLanguageDisplayName(code);
        for (SettingItem it : items) {
            if (it.getActionId() == ACTION_LANGUAGE) {
                it.setSubTitle(name);
                break;
            }
        }
    }

    private void updateSoundSubTitle(boolean isOpen) {
        for (SettingItem it : items) {
            if (it.getActionId() == ACTION_SOUND) {
                it.setSubTitle(getString(isOpen ? R.string.status_on : R.string.status_off));
                break;
            }
        }
    }

    private void updateBrightSubTitle(int percent) {
        for (SettingItem it : items) {
            if (it.getActionId() == ACTION_BRIGHT) {
                it.setSubTitle(percent + "%");
                break;
            }
        }
    }

    /**
     * 提取出来的语言显示名称处理 (把你原来的大段 if-else 搬过来)
     */
    private String getLanguageDisplayName(String code) {
        if (code.contains("zh_CN")) return "简体中文";
        else if (code.contains("zh_TW")) return "繁體中文";
        else if (code.equals("en_US")) return "English";
        else if (code.equals("fr_FR")) return "Français";
        else if (code.equals("de_DE")) return "Deutsch";
        else if (code.equals("ru_RU")) return "Pусский";
        else if (code.equals("ja_JP")) return "日本語";
        else if (code.equals("ko_KR")) return "한국어";
        else if (code.equals("es_ES")) return "Español";
        else if (code.equals("ar_SA")) return "Arabic";
        else if (code.equals("bg_BG")) return "Български";
        else if (code.equals("cs_CZ")) return "Čeština";
        else if (code.equals("da_DK")) return "Dansk";
        else if (code.equals("el_GR")) return "Ελληνικά";
        else if (code.equals("fa_IR")) return "Farsi";
        else if (code.equals("fi_FI")) return "Suomi";
        else if (code.equals("fil_PH")) return "Filipino";
        else if (code.equals("hi_IN")) return "हिंदी";
        else if (code.equals("hr_HR")) return "Hrvatski";
        else if (code.equals("hu_HU")) return "Magyar";
        else if (code.equals("in_ID")) return "Bahasa Indonesia";
        else if (code.equals("it_IT")) return "Italiano";
        else if (code.equals("iw_IL")) return "Hebrew";
        else if (code.equals("lt_LT")) return "Lietuvių";
        else if (code.equals("lv_LT")) return "Latviski";
        else if (code.equals("ms_MY")) return "Bahasa Melayu";
        else if (code.equals("nb_NO")) return "Norsk bokmål";
        else if (code.equals("nl_NL")) return "Nederlands";
        else if (code.equals("pl_PL")) return "Polski";
        else if (code.equals("pt_PT")) return "Português";
        else if (code.equals("ro_RO")) return "Română";
        else if (code.equals("sk_SK")) return "Slovensky";
        else if (code.equals("sl_SI")) return "Slovenski jezik";
        else if (code.equals("sv_SE")) return "Svenska";
        else if (code.equals("th_TH")) return "ไทย";
        else if (code.equals("tr_TR")) return "Türkçe";
        else if (code.equals("uk_UA")) return "Українська";
        else if (code.equals("vi_VN")) return "Tiếng Việt";
        else return "English";
    }

    /**
     * 读取并更新当前系统语言
     */
    private void updateCurrentLanguage() {
        updateLanguageSubTitle();
    }

    /**
     * 读取当前屏幕亮度
     */
    private void readCurrentBrightness() {
        ContentResolver cr = getContentResolver();
        int sysBright;
        try {
            sysBright = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            sysBright = 128;
        }
        int percent = Math.round(sysBright * 100f / 255f);
        percent = Math.max(MIN_BRIGHT, Math.min(MAX_BRIGHT, percent));
        updateBrightSubTitle(percent);
    }

    /**
     * 获取当前 UI 显示的亮度值
     */
    private int getCurrentBrightValue() {
        for (SettingItem it : items) {
            if (it.getActionId() == ACTION_BRIGHT) {
                String text = it.getSubTitle().replace("%", "");
                try {
                    return Integer.parseInt(text);
                } catch (Exception e) {
                    return 50;
                }
            }
        }
        return 50;
    }

    /**
     * 设置亮度
     */
    private void setBrightness(int percent) {
        int sysVal = Math.round(percent * 255f / 100f);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, sysVal);
        android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = sysVal / 255f;
        getWindow().setAttributes(lp);
    }
}