package com.twd.gamesetting;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageActivity extends AppCompatActivity {
    private List<LanguageBean> languageBeans = new ArrayList<>();
    ListView listView;
    private final Context context = this;
    LanguageItemAdapter languageItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        listView = findViewById(R.id.list_language);


        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguageCode = currentLocale.getLanguage()+"_"+currentLocale.getCountry();

        languageBeans.clear();
        Map<String,String> languageMap = new HashMap<>();
        languageMap.put("zh_CN","简体中文");
        languageMap.put("en_US","English");
        languageMap.put("fr_FR","Français");
        languageMap.put("de_DE","Deutsch");
        languageMap.put("ru_RU","Pусский");
        languageMap.put("ja_JP","日本語");
        languageMap.put("ko_KR","한국어");


        List<String> supportedLanguages = Arrays.asList("zh_CN","en_US","fr_FR","de_DE","ru_RU","ja_JP","ko_KR");
        for (String language_sup : supportedLanguages){
            String languageName = languageMap.get(language_sup);
            Log.i("yangxin","------支持的语言-："+languageName);
            LanguageBean languageBean = new LanguageBean(languageName,language_sup,false);
            // 标记当前系统语言为选中
            if (language_sup.equals(currentLanguageCode)){
                languageBean.setSelect(true);
            }
            languageBeans.add(languageBean);
        }
        languageItemAdapter = new LanguageItemAdapter(context,languageBeans);
        listView.setAdapter(languageItemAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            LanguageBean select = languageBeans.get(position);
            String languageCode = select.getLanguageCode(); // "zh_CN" 或 "en_US"
            String[] parts = languageCode.split("_");
            Locale targetLocale;
            if (parts.length == 2) {
                targetLocale = new Locale(parts[0], parts[1]);  // new Locale("zh", "CN")
            } else {
                targetLocale = new Locale(languageCode);  // fallback
            }

            Log.d("yangxin", "切换到语言: " + targetLocale.toString());
            changeSystemLanguage(targetLocale);

            finish();
        });
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageItemAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    class LanguageItemAdapter extends ArrayAdapter<LanguageBean> {
        private LayoutInflater inflater;

        boolean isSelected;

        private int focusedItem = 0;
        public LanguageItemAdapter(@NonNull Context context, List<LanguageBean> languageBeans) {
            super(context, 0,languageBeans);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
            focusedItem = position;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = inflater.inflate(R.layout.item_language, parent, false);
            }
            TextView tvName = itemView.findViewById(R.id.tv_name);
            ImageView ivCheck = itemView.findViewById(R.id.iv_check);

            LanguageBean languageBean = getItem(position);
            if (languageBean != null){
                tvName.setText(languageBean.getLanguageName());
                isSelected = languageBean.isSelect();
                if (isSelected){
                    ivCheck.setVisibility(View.VISIBLE); // 显示勾
                    ivCheck.setImageResource(R.drawable.ic_check_focus);
                }else {
                    ivCheck.setVisibility(View.GONE); // 隐藏勾
                    ivCheck.setImageResource(R.drawable.ic_check_normal);
                }
            }

            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(R.color.bg_focus);
                tvName.setTextColor(ContextCompat.getColor(context,R.color.tv_focus));
                if (languageBean.isSelect()){
                    ivCheck.setImageResource(R.drawable.ic_check_focus);
                }else {
                    ivCheck.setImageResource(R.drawable.unselected);
                }
            }else {
                itemView.setBackgroundResource(R.color.bg_normal);
                tvName.setTextColor(ContextCompat.getColor(context,R.color.tv_normal));
                if (languageBean.isSelect()){
                    ivCheck.setImageResource(R.drawable.ic_check_normal);
                }else {
                    ivCheck.setImageResource(R.drawable.unselected);
                }
            }
            return itemView;
        }
    }

    /*
     * 修改系统语言的方法
     * 需要设置系统app获取系统权限才能执行*/
    public void changeSystemLanguage(Locale locale){
        if (locale != null){
            try {
                Object objIActMag;
                Class clzIActMag = Class.forName("android.app.IActivityManager");
                Class clzActMagNative = Class
                        .forName("android.app.ActivityManagerNative");
                //amn = ActivityManagerNative.getDefault();
                Method mtdActMagNative$getDefault = clzActMagNative
                        .getDeclaredMethod("getDefault");
                objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
                // objIActMag = amn.getConfiguration();
                Method mtdIActMag$getConfiguration = clzIActMag
                        .getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) mtdIActMag$getConfiguration
                        .invoke(objIActMag);
                // set the locale to the new value
                config.locale = locale;
                //持久化  config.userSetLocale = true;
                Class clzConfig = Class
                        .forName("android.content.res.Configuration");
                java.lang.reflect.Field userSetLocale = clzConfig
                        .getField("userSetLocale");
                userSetLocale.set(config, true);
                // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
                // 会重新调用 onCreate();
                Class[] clzParams = {Configuration.class};
                // objIActMag.updateConfiguration(config);
                Method mtdIActMag$updateConfiguration = clzIActMag
                        .getDeclaredMethod("updateConfiguration", clzParams);
                mtdIActMag$updateConfiguration.invoke(objIActMag, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}