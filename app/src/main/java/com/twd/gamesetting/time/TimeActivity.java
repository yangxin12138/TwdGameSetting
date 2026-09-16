package com.twd.gamesetting.time;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.twd.gamesetting.R;
import com.twd.gamesetting.dialog.DatePickerDialog;
import com.twd.gamesetting.dialog.TimePickerDialog;
import com.twd.gamesetting.dialog.TimeZoneDialog;
import com.twd.gamesetting.interfaces.DateSelectedInterface;
import com.twd.gamesetting.interfaces.OnTimeZoneSelectedListener;
import com.twd.gamesetting.interfaces.TimeSelectedInterface;
import com.twd.gamesetting.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeActivity extends AppCompatActivity implements View.OnClickListener ,
        TimeSelectedInterface, DateSelectedInterface
        , OnTimeZoneSelectedListener {
    private final static String TAG = TimeActivity.class.getSimpleName();
    private LinearLayout LL_autoTime;
    private LinearLayout LL_time_item;
    private LinearLayout LL_date_item;
    private LinearLayout LL_time_zone;
    private LinearLayout LL_24Hours_item;

    private TextView tv_time_title;
    private TextView tv_date_title;
    private TextView tv_autoTime;
    private TextView tv_time_summary;
    private TextView tv_date_summary;
    private TextView tv_timeZone_summary;
    private TextView tv_24Hours_summary;

    private TimeZone mCurrentTimeZone;
    private DateTimeUtils utils;
    private boolean mIsNetworkTimeEnabled = false;
    private boolean mIs24HoursEnabled =false;
    // 核心：手动设置的时间戳（用于锁定时间，防止系统覆盖）
    private long mManualSetTimeMillis = 0;
    // 时间同步广播接收器（拦截系统时间更新）
    private BroadcastReceiver mTimeChangeReceiver;
    private Handler timerHandler = new Handler();
    private String switch_on;
    private String switch_off;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        mCurrentTimeZone = TimeZone.getDefault();
        utils = new DateTimeUtils(this);
        mIsNetworkTimeEnabled = utils.isNetworkTimeEnabled();
        mIs24HoursEnabled = utils.is24HoursEnabled();
        switch_on = getResources().getString(R.string.status_on);
        switch_off = getResources().getString(R.string.status_off);
        registerTimeChangeReceiver();
        initView();
        updateTimeRunnable.run();
    }

    private void initView(){
        //LL
        LL_autoTime = findViewById(R.id.ll_autoTime);
        LL_time_item = findViewById(R.id.ll_time_item);
        LL_date_item = findViewById(R.id.ll_date_item);
        LL_time_zone = findViewById(R.id.ll_time_zone_item);
        LL_24Hours_item = findViewById(R.id.ll_24Hours_item);

        tv_time_title = findViewById(R.id.tv_time_title);
        tv_date_title = findViewById(R.id.tv_date_title);
        //summary
        tv_autoTime = findViewById(R.id.tv_autoTime_status);
        tv_time_summary = findViewById(R.id.tv_time_summary);
        tv_date_summary = findViewById(R.id.tv_date_summary);
        tv_timeZone_summary = findViewById(R.id.tv_time_zone_summary);
        tv_24Hours_summary = findViewById(R.id.tv_24Hours_summary);

        tv_autoTime.setText( mIsNetworkTimeEnabled ? switch_on : switch_off);
        tv_24Hours_summary.setText(mIs24HoursEnabled ? switch_on : switch_off);
        LL_autoTime.setOnClickListener(this);
        LL_time_item.setOnClickListener(this);
        LL_date_item.setOnClickListener(this);
        LL_time_zone.setOnClickListener(this);
        LL_24Hours_item.setOnClickListener(this);

        LL_autoTime.requestFocus();
        refreshSwitch(mIsNetworkTimeEnabled);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.ll_autoTime){
            mIsNetworkTimeEnabled = !mIsNetworkTimeEnabled;
            setUseNetworkTime(mIsNetworkTimeEnabled);
            setUseAutoTimeZone(mIsNetworkTimeEnabled);
            tv_autoTime.setText( mIsNetworkTimeEnabled ? switch_on : switch_off);
            if (mIsNetworkTimeEnabled) {
                mManualSetTimeMillis = 0;
            }
            refreshSwitch(mIsNetworkTimeEnabled);
        } else if (v.getId() == R.id.ll_time_item) {
            showTimeDialog();
        } else if (v.getId() == R.id.ll_date_item) {
            showDateDialog();
        } else if (v.getId() == R.id.ll_time_zone_item) {
            showTimeZoneDialog();
        } else if (v.getId() == R.id.ll_24Hours_item) {
            mIs24HoursEnabled = !mIs24HoursEnabled;
            tv_24Hours_summary.setText(mIs24HoursEnabled ? switch_on : switch_off);
            setUse24HoursTime(mIs24HoursEnabled);
        }
    }
    private void showTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this,this);
        dialog.show();
    }

    private void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,this);
        dialog.show();
    }

    private void showTimeZoneDialog(){
        TimeZoneDialog dialog = new TimeZoneDialog(this,this);
        dialog.show();
    }
    private void getSystemTime(){
        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(mCurrentTimeZone);
        Date currentDate = calendar.getTime();
        //设置日期的格式
        SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
        String formatterDate = dateFormat.format(currentDate);

        String timeFormatString = DateTimeUtils.getTimeFormat(this);
        //设置时间的格式
        DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
        timeFormat.setTimeZone(mCurrentTimeZone); // 时间也绑定时区
        String formatterTime = timeFormat.format(currentDate);

        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(mCurrentTimeZone.getID());

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName;
        //在TextView上更新日期和时间
        tv_time_summary.setText(formatterTime);
        tv_date_summary.setText(formatterDate);
        tv_timeZone_summary.setText(timeZoneInfo);
    }

    private void setUseNetworkTime(boolean enabled){
        // 强制设置自动时间开关（系统应用有权限）
        Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME, enabled? 1:0);
        Settings.Global.putString(getContentResolver(), "auto_time", enabled ? "1" : "0");
        Log.i(TAG, "setUseNetworkTime: 网络时间" + (enabled ? "启用" : "禁用"));
    }
    private void setUse24HoursTime(boolean enabled){
        Settings.System.putInt(getContentResolver(),Settings.System.TIME_12_24,enabled ? 24 : 12);
        Log.i(TAG, "setUse24HoursTime: 24小时制" + (enabled ? "启用" : "禁用"));
    }
    private void setUseAutoTimeZone(boolean enabled) {
        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, enabled ? 1 : 0);
            Settings.Global.putString(getContentResolver(), "timezone.auto", enabled ? "1" : "0");
            Log.i(TAG, "setUseAutoTimeZone: 自动时区" + (enabled ? "启用" : "禁用"));
        } catch (SecurityException e) {
            Log.e(TAG, "setUseAutoTimeZone: 权限异常", e);
        }
    }
    private SimpleDateFormat getDateFormatterByTimeZone(TimeZone timeZone) {
        SimpleDateFormat sdf;
        boolean isEast8Zone = timeZone.getRawOffset() == TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();

        if (Locale.CHINESE.getLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            sdf = new SimpleDateFormat(isEast8Zone ? "yyyy/MM/dd" : "dd/MM/yyyy", Locale.CHINA);
        } else {
            sdf = new SimpleDateFormat(isEast8Zone ? "yyyy/MM/dd" : "dd/MM/yyyy", Locale.US);
        }
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    @Override
    public void onDateSelected(String time) {
        try {
            // 使用"/"分割日期字符串
            String[] parts = time.split("/");
            if (parts.length == 3) {
                // 分别解析年、月、日
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                Calendar calendar =  Calendar.getInstance();
                calendar.setTimeZone(mCurrentTimeZone);
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                long when = calendar.getTimeInMillis();
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setTime(when);
                    Log.i(TAG, "onDateSelected: 手动设置日期成功 → " + time);
                }
                // 2. 缓存手动设置的时间戳（核心！用于拦截后恢复）
                mManualSetTimeMillis = when;

                // 3. 立即更新UI
                SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
                String formattedDate = dateFormat.format(calendar.getTime());
                tv_date_summary.setText(formattedDate);

            } else {
                // 如果日期格式不正确，抛出异常或处理错误
                throw new IllegalArgumentException("Date format should be yyyy/MM/dd");
            }
        }catch (Exception e) {
            Log.e(TAG, "onDateSelected: 设置日期异常", e);
        }
    }

    @Override
    public void onTimeZoneSelected(String timeZoneId) {
        mCurrentTimeZone = TimeZone.getTimeZone(timeZoneId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(mCurrentTimeZone);
        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(timeZoneId);

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName +"\n"+ " GMT " +
                String.format("%s%02d:%02d",
                        mCurrentTimeZone.getRawOffset() >= 0 ? "+" : "-",
                        Math.abs(mCurrentTimeZone.getRawOffset()) / 3600000,
                        Math.abs(mCurrentTimeZone.getRawOffset() % 3600000) / 60000);
        tv_timeZone_summary.setText(timeZoneInfo);

        getSystemTime();
    }

    @Override
    public void onTimeSelected(String time) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeZone(mCurrentTimeZone);

        SimpleDateFormat timeParser = mIs24HoursEnabled
                ? new SimpleDateFormat("HH:mm", Locale.getDefault())
                : new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date selectedTime = timeParser.parse(time);
            if (selectedTime == null) {
                Log.e(TAG, "onTimeSelected: 时间解析失败，格式不匹配 | 选择的时间=" + time + " | 解析格式=" + timeParser.toPattern());
                return;
            }

            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTime(selectedTime);
            int targetHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int targetMinute = selectedCalendar.get(Calendar.MINUTE);

            Log.i(TAG, "onTimeSelected: 解析结果 → 24小时制小时=" + targetHour + ", 分钟=" + targetMinute);

            Calendar currentCalendar = Calendar.getInstance();
            targetCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            targetCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            targetCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
            targetCalendar.set(Calendar.HOUR_OF_DAY, targetHour);
            targetCalendar.set(Calendar.MINUTE, targetMinute);
            targetCalendar.set(Calendar.SECOND, 0);
            targetCalendar.set(Calendar.MILLISECOND, 0);

            long newTimeInMillis = targetCalendar.getTimeInMillis();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                try {
                    alarmManager.setTime(newTimeInMillis);
                    Log.i(TAG, "onTimeSelected: 系统时间设置成功 | 时间戳=" + newTimeInMillis);
                } catch (SecurityException e) {
                    Log.e(TAG, "onTimeSelected: 设置系统时间失败！缺少SET_TIME权限或非系统应用", e);
                    return;
                }
            } else {
                Log.e(TAG, "onTimeSelected: 获取AlarmManager失败");
                return;
            }
// 2. 缓存手动设置的时间戳（核心！用于拦截后恢复）
            mManualSetTimeMillis = newTimeInMillis;
            // 步骤6：更新UI显示（适配当前的12/24小时制格式）
            String timeFormatString = DateTimeUtils.getTimeFormat(this); // 复用原有工具类的格式
            DateFormat displayFormat = new SimpleDateFormat(timeFormatString, Locale.getDefault());
            displayFormat.setTimeZone(mCurrentTimeZone);
            tv_time_summary.setText(displayFormat.format(targetCalendar.getTime()));

        } catch (ParseException e) {
            Log.e(TAG, "onTimeSelected: 解析异常", e);
        }
    }

    // ========== 注册时间变化广播，拦截系统自动同步 ==========
    private void registerTimeChangeReceiver() {
        mTimeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // 拦截系统时间/日期/时区变化广播
                if (Intent.ACTION_TIME_CHANGED.equals(action)
                        || Intent.ACTION_DATE_CHANGED.equals(action)
                        || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                    // 仅在关闭网络时间且有手动设置的时间时，恢复手动时间
                    if (!mIsNetworkTimeEnabled && mManualSetTimeMillis > 0) {
                        Log.i(TAG, "拦截到系统时间变化，恢复手动设置的时间");
                        resetToManualTime();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeChangeReceiver, filter);
    }

    // ========== 恢复手动设置的时间（拦截系统同步后调用） ==========
    private void resetToManualTime() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && mManualSetTimeMillis > 0) {
            try {
                alarmManager.setTime(mManualSetTimeMillis);
                Log.i(TAG, "resetToManualTime: 恢复手动时间成功，时间戳=" + mManualSetTimeMillis);
                // 立即更新UI
                getSystemTime();
            } catch (SecurityException e) {
                Log.e(TAG, "resetToManualTime: 恢复时间失败", e);
            }
        }
    }
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            getSystemTime();
            //每隔1秒更新一次时间
            timerHandler.postDelayed(this,1000);
        }
    };
    private void refreshSwitch(boolean mIsNetworkTimeEnabled){
        if (mIsNetworkTimeEnabled){
            int gray = getResources().getColor(R.color.auto_time_checked);
            tv_time_title.setTextColor(gray);
            tv_time_summary.setTextColor(gray);
            tv_date_title.setTextColor(gray);
            tv_date_summary.setTextColor(gray);

            LL_time_item.setFocusable(false);
            LL_date_item.setFocusable(false);
        }else {
            ColorStateList csl = ContextCompat.getColorStateList(this, R.color.selector_time_text);
            tv_time_title.setTextColor(csl);
            tv_time_summary.setTextColor(csl);
            tv_date_title.setTextColor(csl);
            tv_date_summary.setTextColor(csl);

            LL_time_item.setFocusable(true);
            LL_date_item.setFocusable(true);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimeRunnable);
        // 注销广播接收器
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }
}