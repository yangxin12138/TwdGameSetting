package com.twd.gamesetting.dialog;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.twd.gamesetting.R;
import com.twd.gamesetting.interfaces.OnTimeZoneSelectedListener;
import com.twd.gamesetting.time.TimeActivity;
import com.twd.gamesetting.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:12 2024/6/21
 */
public class TimeZoneDialog extends Dialog {

    private ListView listViewTimeZones;
    private TextView textViewTitle;
    TimeZoneAdapter timezoneAdapter;
    private OnTimeZoneSelectedListener onTimeZoneSelectedListener;
    Context context;
    Map<String, String> timeZones = new HashMap<>();
    public TimeZoneDialog(@NonNull Context context, OnTimeZoneSelectedListener onTimeZoneSelectedListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
        this.onTimeZoneSelectedListener = onTimeZoneSelectedListener;
    }

    DateTimeUtils dateTimeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timezone_listview);
        dateTimeUtils = new DateTimeUtils(context);
        //初始化控件
        textViewTitle = findViewById(R.id.dialog_title);
        listViewTimeZones = findViewById(R.id.listView_TimeZone);
        timeZones = dateTimeUtils.getTimeZoneList();

        timezoneAdapter = new TimeZoneAdapter(context, timeZones);
        listViewTimeZones.setAdapter(timezoneAdapter);
        listViewTimeZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timezoneAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewTimeZones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String timeZoneId = (String) timezoneAdapter.getItem(position);
                String timeZoneName = dateTimeUtils.getTimeZoneList().get(timeZoneId);
                Log.i("yangxin", "onItemClick: 点击获取时区 = "+timeZoneId+",Name ="+timeZoneName);

                setTimeZone(timeZoneId);
                if (onTimeZoneSelectedListener != null){
                    onTimeZoneSelectedListener.onTimeZoneSelected(timeZoneId);
                }
                dismiss();
                Intent restartIntent = new Intent(context.getApplicationContext(), TimeActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(restartIntent);
            }
        });
        listViewTimeZones.requestFocus();
    }

    private void setTimeZone(String timeZone){
        TimeZone tz =  TimeZone.getTimeZone(timeZone);
        Log.i("yangxin", "setTimeZone: 修改之前，时区是"+timeZone);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(timeZone);

        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        //设置日期的格式
        TimeZone timeZone1 = calendar.getTimeZone();
        String timeZoneId = timeZone1.getID();
        Log.i("yangxin", "setTimeZone: 修改完了，时区是"+timeZoneId);
    }
}
