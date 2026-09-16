package com.twd.gamesetting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twd.gamesetting.R;
import com.twd.gamesetting.interfaces.TimeSelectedInterface;
import com.twd.gamesetting.time.TimeActivity;
import com.twd.gamesetting.utils.DateTimeUtils;


import java.util.Calendar;
import java.util.Locale;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:10 2024/6/21
 */
public class TimePickerDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener{
    String selectTime;
    private TimePicker timePicker;
    private Context mContext;
    private final static String TAG = "TimePickerDialog";
    private TimeSelectedInterface timeSelectedInterface;
    private DateTimeUtils utils;

    public TimePickerDialog(@NonNull Context context, TimeSelectedInterface timeSelectedInterface) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        this.timeSelectedInterface = timeSelectedInterface;
    }
    public TimePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected TimePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_picker);
        if (getWindow() != null) {getWindow().setLayout((int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);}
        timePicker = findViewById(R.id.timePicker);
        utils = new DateTimeUtils(mContext);
        Calendar now = Calendar.getInstance();
        timePicker.setIs24HourView(utils.is24HoursEnabled());
        if (utils.is24HoursEnabled()){
            timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
        }else {
            int hour12 = now.get(Calendar.HOUR);
            hour12 = hour12 == 0 ? 12 : hour12;
            timePicker.setCurrentHour(hour12);
            timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
        }
        View hourView = timePicker.findViewById(Resources.getSystem().getIdentifier("hour", "id", "android"));
        View minuteView = timePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        View amPmView = timePicker.findViewById(Resources.getSystem().getIdentifier("amPm", "id", "android"));
        timePicker.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        initSelectTime();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                selectTime = String.format("%02d:%02d", hourOfDay, minute);
                if(view.is24HourView()){
                    //24小时制
                    selectTime = String.format("%02d:%02d", hourOfDay, minute);
                    Log.i(TAG, "onTimeChanged: 24-hour format time is " + selectTime);
                }else {
                    // 12小时制，需要获取AM/PM
                    int currentHour = view.getCurrentHour(); // 12小时制小时（1-12）
                    boolean isPM = currentHour == 12 ? now.get(Calendar.AM_PM) == Calendar.PM : currentHour >= 12;

                    Locale currentLocale = mContext.getResources().getConfiguration().locale;
                    String period = "";
                    if (Locale.CHINESE.getLanguage().equals(currentLocale.getLanguage())) {
                        period = isPM ? "下午" : "上午";
                    } else {
                        period = isPM ? "PM" : "AM";
                    }

                    int displayHour = currentHour % 12 == 0 ? 12 : currentHour % 12;
                    selectTime = String.format("%02d:%02d %s", displayHour, minute, period);
                    Log.i(TAG, "onTimeChanged: 12-hour format time is " + selectTime);
                }
            }
        });
        timePicker.post(new Runnable() {
            @Override
            public void run() {
                timePicker.requestFocus();
            }
        });
        hourView.setOnClickListener(this); minuteView.setOnClickListener(this); amPmView.setOnClickListener(this);
        hourView.setOnFocusChangeListener(this); minuteView.setOnFocusChangeListener(this); amPmView.setOnFocusChangeListener(this);
    }

    private void initSelectTime() {
        if (timePicker.is24HourView()) {
            selectTime = String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        } else {
            // 12小时制：先判定AM/PM
            Calendar now = Calendar.getInstance();
            boolean isPM = timePicker.getCurrentHour() == 12 ? now.get(Calendar.AM_PM) == Calendar.PM : timePicker.getCurrentHour() >= 12;
            Locale currentLocale = mContext.getResources().getConfiguration().locale;
            String period = Locale.CHINESE.getLanguage().equals(currentLocale.getLanguage())
                    ? (isPM ? "下午" : "上午")
                    : (isPM ? "PM" : "AM");
            int displayHour = timePicker.getCurrentHour() % 12 == 0 ? 12 : timePicker.getCurrentHour() % 12;
            selectTime = String.format("%02d:%02d %s", displayHour, timePicker.getCurrentMinute(), period);
        }
        Log.i(TAG, "initSelectTime: 初始时间 = " + selectTime);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: selectTime = " + selectTime);
        if (timeSelectedInterface != null) {
            timeSelectedInterface.onTimeSelected(selectTime);
        }
        dismiss();
        Intent restartIntent = new Intent(mContext.getApplicationContext(), TimeActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(restartIntent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            v.setBackgroundResource(R.drawable.dialog_item_background);
        }else {
            v.setBackgroundResource(0);
        }
    }
}
