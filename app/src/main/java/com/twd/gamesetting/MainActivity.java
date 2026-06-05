package com.twd.gamesetting;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
}